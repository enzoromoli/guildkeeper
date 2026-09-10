package fr.dev.sensei.guild.keeper.finance;

import fr.dev.sensei.guild.keeper.recruitment.Member;
import fr.dev.sensei.guild.keeper.recruitment.MemberRank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GuildFinanceServiceTest {

    @Mock
    GuildAccountRepository accountRepository;
    @InjectMocks
    GuildFinanceService service;

    @Test
    void should_increase_balance_and_persist_account_when_deposit_is_valid() {
        // Arrange : un GuildAccount de solde connu
        GuildAccount guildAccount = new GuildAccount("0", 0);

        // Act : service.deposit(account, montant)
        service.deposit(guildAccount, 123);

        // Assert : nouveau solde attendu + verify(accountRepository).save(...)
        assertThat(guildAccount.balance()).isEqualTo(123);
        verify(accountRepository).save(guildAccount);
    }

    @Test
    void should_decrease_balance_and_persist_account_when_distribute_is_valid() {
        // Arrange : un GuildAccount de solde connu
        GuildAccount guildAccount = new GuildAccount("0", 100);

        // Act : service.distributeLoot(account, montant)
        service.distributeLoot(guildAccount, 50);

        // Assert : nouveau solde attendu + verify(accountRepository).save(...)
        assertThat(guildAccount.balance()).isEqualTo(50);
        verify(accountRepository).save(guildAccount);
    }

    @Test
    void should_return_true_if_account_solvency_is_valid(){
        // Arrange : un GuildAccount de solde connu
        GuildAccount guildAccount = new GuildAccount("0", 100);

        // Act : service.distributeLoot(account, montant)
        service.checkSolvency(guildAccount, 87);

        // Assert : nouveau solde attendu + verify(accountRepository).save(...)
        assertThat(guildAccount.balance()).isEqualTo(100);
        verify(accountRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -7, 0})
    void should_throw_InvalidAmountException_when_deposit_amount_is_not_positive(int amount) {
        // Arrange : un GuildAccount de solde connu
        GuildAccount guildAccount = new GuildAccount("0", 100);

        // Act + Assert : assertThatThrownBy(...).isInstanceOf(InvalidAmountException.class)
        assertThatThrownBy(() -> service.deposit(guildAccount, amount)).isInstanceOf(InvalidAmountException.class);

        // Assert : solde inchangé, aucune interaction avec le repository
        assertThat(guildAccount.balance()).isEqualTo(100);
        verify(accountRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 7, 154})
    void should_throw_InsufficientFundsException_when_distributeLoot_amount_is_less_than_balance(int amount) {
        // Arrange : un GuildAccount de solde connu
        GuildAccount guildAccount = new GuildAccount("0", 0);

        // Act + Assert : assertThatThrownBy(...).isInstanceOf(InvalidAmountException.class)
        assertThatThrownBy(() -> service.distributeLoot(guildAccount, amount)).isInstanceOf(InsufficientFundsException.class);

        // Assert : solde inchangé, aucune interaction avec le repository
        assertThat(guildAccount.balance()).isEqualTo(0);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void should_distribute_dividends_nothing_when_guild_has_no_members() {
        //Arrange
        GuildAccount account = new GuildAccount("g-1", 1_000);

        //Act
        Map<Member, Integer> shares = service.distributeDividends(account, List.of(), 10);

        //Assert
        assertThat(shares).isEmpty();
        assertThat(account.balance()).isEqualTo(1_000);
    }

    @Test
    void should_distribute_dividends_all_to_the_only_member(){
        //Arrange
        GuildAccount account = new GuildAccount("g-1", 1_000);
        Member member = new Member("0", "Albert", MemberRank.NOVICE, 0, 1);

        //Act
        Map<Member, Integer> shares = service.distributeDividends(account, List.of(member), 10);

        //Assert
        assertThat(shares).hasSize(1);
        assertThat(shares).containsKey(member);
        assertThat(shares.get(member)).isEqualTo(100);
        assertThat(account.balance()).isEqualTo(900);
    }
    @Test
    void should_distribute_dividends_all_proportionally_to_weight(){
        //Arrange
        GuildAccount account = new GuildAccount("g-1", 1_000);
        Member member1 = new Member("0", "Albert", MemberRank.APPRENTICE, 0, 1);
        Member member2 = new Member("1", "Nicolas", MemberRank.ELITE, 0, 1);

        List<Member> members = List.of(member1, member2);

        //Act
        Map<Member, Integer> shares = service.distributeDividends(account, members, 10);

        //Assert
        assertThat(shares).hasSize(2);
        assertThat(shares).containsKey(member1);
        assertThat(shares).containsKey(member2);
        assertThat(shares.get(member1)).isEqualTo(33);
        assertThat(shares.get(member2)).isEqualTo(66);
        assertThat(account.balance()).isEqualTo(901);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5, 101})
    void should_reject_invalid_amount(int amount){
        //Arrange
        GuildAccount account = new GuildAccount("g-1", 1_000);
        Member member1 = new Member("0", "Albert", MemberRank.APPRENTICE, 0, 1);
        Member member2 = new Member("1", "Nicolas", MemberRank.ELITE, 0, 1);

        List<Member> members = List.of(member1, member2);

        //Act & Assert
        assertThatThrownBy(() -> service.distributeDividends(account, members, amount))
            .isInstanceOf(InvalidAmountException.class);
        assertThat(account.balance()).isEqualTo(1_000);
    }

    // @Test
    void should_reject_distribute_dividends_when_guild_balance_is_insufficient() {
        // p is a percentage. It should never append to have an insufficient balance if
        // the amount taken by members is a % of the balance because the max % is 100% and it give the whole balance.
    }
}
