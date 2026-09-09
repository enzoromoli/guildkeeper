package fr.dev.sensei.guild.keeper.missions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LootCalculatorTest {

    private final LootCalculator lootCalculator = new LootCalculator();

    @ParameterizedTest(name = "baseLootValue={0}, luck={1} -> {2}")
    @MethodSource("lootScenarios")
    void should_calculate_loot_for_various_luck_and_base_values(int baseLootValue, int luck, int expectedLoot) {
        // Act
        int loot = lootCalculator.calculateLoot(baseLootValue, luck);

        // Assert
        assertThat(loot).isEqualTo(expectedLoot);
    }

    static Stream<Arguments> lootScenarios() {
        return Stream.of(
            Arguments.of(100, 2, 110),
            Arguments.of(100, 5, 125),
            Arguments.of(100,10,150)
        );
    }
}
