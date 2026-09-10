package fr.dev.sensei.guild.keeper.experience;

import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Chapitre 5 — live coding « TDD sur le calcul de niveau » — <b>à développer entièrement en TDD</b>.
 *
 * <p>La classe de production {@code LevelCalculator} n'existe pas encore : c'est le seul
 * exercice vraiment "from scratch" du cours. On la fait naitre d'un test, en suivant le
 * cycle red / green / refactor.
 *
 * <p>Règle visée : {@code levelFor(int experiencePoints)} renvoie le niveau d'affichage
 * d'un membre, dérivé de ses points d'expérience.
 * <ul>
 *   <li>0 XP -> niveau 1 ;</li>
 *   <li>+1 niveau tous les 100 XP (100 -> 2, 250 -> 3, ...) ;</li>
 *   <li>expérience négative -> {@link IllegalArgumentException}.</li>
 * </ul>
 *
 * <p>Classe à créer dans ce package ({@code experience}), non câblée au reste du domaine.
 * Une fois un test écrit, retirer {@code @Tag("todo")} et le {@code fail(...)}.
 */
class LevelCalculatorTest {

    private final LevelCalculator levelCalculator = new LevelCalculator();

    @Test
    void should_return_level_1_for_zero_experience() throws ExecutionControl.NotImplementedException {
        //Act & Assert
        assertEquals(1, levelCalculator.calculateLevel(0));
    }

    @Test
    void should_return_level_2_from_100_experience_points() throws ExecutionControl.NotImplementedException {
        assertEquals(2, levelCalculator.calculateLevel(100));
    }

    @Test
    void should_reject_negative_experience() {
        assertThatThrownBy(() -> levelCalculator.calculateLevel(-1))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> levelCalculator.calculateLevel(-57))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
