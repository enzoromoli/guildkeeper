import { describe, it, expect, vi } from 'vitest';
import { createGuildKeeperClient, NotFoundError } from '../../src';
import { fetchedUrl, jsonResponse } from './httpTestSupport.ts';

const BASE = 'http://api.test';

function clientWith(response: Response) {
  const fetchImpl = vi.fn().mockResolvedValue(response);
  return { client: createGuildKeeperClient({ baseUrl: BASE, fetchImpl }), fetchImpl };
}

describe('members (asynchrone) — TP chapitre 3', () => {

  it('members.assignments(name) résout la liste des attributions', async () => {
    //Arrange
    const assignments = [
      {questId: '1', questTitle: 'Nettoyer les caves de la guilde', status: 'COMPLETED'},
      {questId: '2', questTitle: 'Escorter la caravane marchande', status: 'ASSIGNED'},
    ]
    const { client, fetchImpl } = clientWith(jsonResponse(assignments));

    //Act
    const result = await client.members.assignments('Dragan');

    //Assert
    expect(fetchedUrl(fetchImpl)).toBe('http://api.test/api/v1/members/Dragan/assignments');
    expect(result).toHaveLength(2);
    expect(result.map((a) => a.status)).toEqual(['COMPLETED', 'ASSIGNED']);
  });

  it('members.get(name) rejette avec NotFoundError pour un membre inconnu', async () => {
    //Arrange
    const members = [
      { id: '1', name: 'Dragan', rank: 'NOVICE', experiencePoints: 1, luck: 1 },
      { id: '1', name: 'Paula', rank: 'NOVICE', experiencePoints: 1, luck: 1 },
    ];
    const { client } = clientWith(jsonResponse(members));

    //Act & Assert
    await expect(client.members.get('Gandalf')).rejects.toBeInstanceOf(NotFoundError);
  });
});
