package fr.dev.sensei.guild.keeper.finance;

import fr.dev.sensei.guild.keeper.recruitment.Member;
import fr.dev.sensei.guild.keeper.recruitment.MemberRank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.floor;

/**
 * Operations financieres sur le compte d'une guilde.
 *
 * <p>Perimetre volontairement limite a ce qui est specifie : depot, distribution
 * de butin, verification de solvabilite. La distribution de dividendes par rang
 * n'est PAS implementee ici : elle est a developper en TDD par les etudiants.
 * Les instructions a suivre sont donnees avec le projet final.
 */
public class GuildFinanceService {

    private final GuildAccountRepository guildAccountRepository;

    public GuildFinanceService(GuildAccountRepository guildAccountRepository) {
        this.guildAccountRepository = guildAccountRepository;
    }

    /**
     * Credite le compte de la guilde.
     *
     * @throws InvalidAmountException si {@code amount <= 0}
     */
    public void deposit(GuildAccount account, int amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        account.increaseBy(amount);
        guildAccountRepository.save(account);
    }

    /**
     * Debite le compte du montant de butin distribue a un membre.
     *
     * @throws InsufficientFundsException si le compte n'est pas solvable pour ce montant
     */
    public void distributeLoot(GuildAccount account, int amount) {
        if (!checkSolvency(account, amount)) {
            throw new InsufficientFundsException(account.balance(), amount);
        }
        account.decreaseBy(amount);
        guildAccountRepository.save(account);
    }

    /** @return {@code true} si le solde couvre {@code amount}. */
    public boolean checkSolvency(GuildAccount account, int amount) {
        return account.balance() >= amount;
    }

    /**
     * Distribute dividends for a guild & its members
     * @param account GuildAccount
     * @param members List<Member>
     * @param percentage int
     * @return Map<Member, Integer>
     */
    public Map<Member, Integer> distributeDividends(GuildAccount account, List<Member> members, int percentage) {
        if(percentage <= 0 || percentage > 100) {
            throw new InvalidAmountException(percentage);
        }

        Map<Member, Integer> result = new HashMap<>();
        int totalNeededToBeDelivered = (int) floor((double) account.balance() * percentage / 100);

        int totalWeight = 0;
        for(Member member : members){
            totalWeight += getWeightFromRank(member.rank());
        }

        int totalDelivered = 0;
        for(Member member : members){
            int memberAmountDelivered = (int) floor((double) totalNeededToBeDelivered * getWeightFromRank(member.rank()) / totalWeight);
            totalDelivered += memberAmountDelivered;
            result.put(member, memberAmountDelivered);
        }

        account.decreaseBy(totalDelivered);
        return result;
    }

    /**
     * Calculate weight of a member rank to calculate dividends
     * @param rank MemberRank
     * @return int
     */
    private int getWeightFromRank(MemberRank rank) {
        int rankStepsAboveNovice = rank.ordinal() - MemberRank.NOVICE.ordinal();
        return rankStepsAboveNovice + 1;
    }
}
