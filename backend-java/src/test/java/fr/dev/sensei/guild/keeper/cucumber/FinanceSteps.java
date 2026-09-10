package fr.dev.sensei.guild.keeper.cucumber;

import fr.dev.sensei.guild.keeper.finance.*;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.cucumber.java.fr.Soit;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class FinanceSteps {

    private GuildAccountRepository accountRepository;
    private GuildFinanceService financeService;
    private Throwable caughtException;

    @Soit("un système de guilde")
    public void une_guilde_vide() {
        accountRepository = new InMemoryGuildAccountRepository();
        financeService = new GuildFinanceService(accountRepository);
        caughtException = null;
    }

    @Soit("{string} guilde déjà existante avec une balance de {int}")
    public void guilde_deja_existante_with_balance(String name, int balance) {
        accountRepository.save(new GuildAccount(name, balance));
        assertThat(accountRepository.findByGuildId(name)).isPresent();
    }

    @Quand("{string} essaye de distribuer {int} de loot")
    public void guilde_try_distribute_loot(String name, int lootAmount) {
        Optional<GuildAccount> guildAccount = accountRepository.findByGuildId(name);
        assertThat(guildAccount.isPresent()).isTrue();
        caughtException = catchThrowable(()->financeService.distributeLoot(guildAccount.get(), lootAmount));
    }

    @Alors("la distribution est rejeté pour cause de balance trop basse")
    public void la_distribution_est_rejete_pour_cause_de_balance_trop_basse() {
        assertThat(caughtException).isInstanceOf(InsufficientFundsException.class);
    }

    @Alors("la balance de la guilde {string} sera de {int}")
    public void la_distribution_est_reussi_et_reduite_du_montant_demande(String name, int balanceAmount) {
        Optional<GuildAccount> guildAccount = accountRepository.findByGuildId(name);
        assertThat(guildAccount.isPresent()).isTrue();
        assertThat(guildAccount.get().balance()).isEqualTo(balanceAmount);
    }
}
