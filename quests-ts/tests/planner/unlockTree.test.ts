import { describe, it, expect } from 'vitest';
import {
  availableQuests,
  findQuestById,
  findQuestByTitle,
  isQuestUnlocked,
  NotFoundError,
} from '../../src';
import type { QuestDto } from '../../src';
import { todo } from '../todo';
import questsFixture from '../../fixtures/quests.json';

const quests = questsFixture as QuestDto[];
const [cellar, caravan, dragon] = quests;

describe('findQuestById / findQuestByTitle', () => {
  it('returns the matching quest when it exists', () => {
    expect(findQuestById(quests, '2')?.title).toBe('Escorter la caravane marchande');
    expect(findQuestByTitle(quests, 'Terrasser le dragon des cimes')?.id).toBe('3');
  });

  it('returns undefined when the list is empty', () => {
    //Arrange
    const quests = [] as QuestDto[];

    //Act
    const result1 = findQuestById(quests, '0');
    const result2 = findQuestByTitle(quests, 'xxx');

    //Assert
    expect(result1).toBeUndefined();
    expect(result2).toBeUndefined();
  });
});

describe('isQuestUnlocked', () => {
  it('unlocks a quest that has no prerequisite', () => {
    expect(isQuestUnlocked(cellar!, [])).toBe(true);
  });

  it('unlocks a quest whose prerequisite has been completed', () => {
    expect(isQuestUnlocked(caravan!, ['1'])).toBe(true);
  });

  it('locks a quest whose prerequisite has not been completed', () => {
    expect(isQuestUnlocked(caravan!, [])).toBe(false);
  });
});

describe('availableQuests', () => {
  it('lists only unlocked, not-yet-completed quests', () => {
    expect(availableQuests(quests, []).map((q) => q.id)).toEqual(['1']);
    expect(availableQuests(quests, ['1']).map((q) => q.id)).toEqual(['2']);
    expect(availableQuests(quests, ['1', '2']).map((q) => q.id)).toEqual(['3']);
    expect(availableQuests(quests, ['1', '2', '3'])).toEqual([]);
  });

  it('ignores the dragon quest until the caravan is done', () => {
    expect(availableQuests(quests, ['1']).map((q) => q.title)).not.toContain(dragon!.title);
  });
});
