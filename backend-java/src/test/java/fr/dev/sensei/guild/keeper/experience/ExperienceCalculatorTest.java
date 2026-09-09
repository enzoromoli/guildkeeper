package fr.dev.sensei.guild.keeper.experience;

import fr.dev.sensei.guild.keeper.missions.Quest;
import fr.dev.sensei.guild.keeper.missions.QuestAssignment;
import fr.dev.sensei.guild.keeper.missions.QuestAssignmentStatus;
import fr.dev.sensei.guild.keeper.missions.QuestDifficulty;
import fr.dev.sensei.guild.keeper.recruitment.Member;
import fr.dev.sensei.guild.keeper.recruitment.MemberRank;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExperienceCalculatorTest {

    private final ExperienceCalculator calculator = new ExperienceCalculator();

    private static QuestAssignment notCompletedQuestFor(MemberRank rank, QuestDifficulty difficulty) {
        Member member = new Member("m-" + rank, "Dragan", rank, 0, 5);
        Quest quest = Quest.standalone("q-2", "Nettoyer les caves pullulantes", difficulty, 100, 40);
        return new QuestAssignment(member, quest, QuestAssignmentStatus.ABANDONED);
    }

    private static QuestAssignment completedQuestFor(MemberRank rank, QuestDifficulty difficulty) {
        Member member = new Member("m-" + rank, "Dragan", rank, 0, 5);
        Quest quest = Quest.standalone("q-1", "Nettoyer les caves", difficulty, 100, 40);
        return new QuestAssignment(member, quest, QuestAssignmentStatus.COMPLETED);
    }

    private static QuestAssignment completedEasyQuestFor(MemberRank rank) {
        return completedQuestFor(rank, QuestDifficulty.EASY);
    }

    private static QuestAssignment completedLegendaryQuestFor(MemberRank rank) {
        return completedQuestFor(rank, QuestDifficulty.LEGENDARY);
    }

    @Test
    void should_grant_base_experience_when_member_is_novice() {
        // Arrange
        QuestAssignment assignment = completedEasyQuestFor(MemberRank.NOVICE);

        // Act
        int reward = calculator.calculateExperienceReward(assignment);

        // Assert
        assertThat(reward).isEqualTo(100);
    }

    @Test
    void should_grant_10_percent_bonus_when_member_is_apprentice() {
        // Arrange
        QuestAssignment assignment = completedEasyQuestFor(MemberRank.APPRENTICE);

        // Act
        int reward = calculator.calculateExperienceReward(assignment);

        // Assert
        assertThat(reward).isEqualTo(110);
    }

    @Test
    void should_grant_20_percent_bonus_when_member_is_veteran() {
        // Arrange
        QuestAssignment assignment = completedEasyQuestFor(MemberRank.VETERAN);

        // Act
        int reward = calculator.calculateExperienceReward(assignment);

        // Assert
        assertThat(reward).isEqualTo(120);
    }

    @Test
    void should_grant_30_percent_bonus_when_member_is_elite() {
        // Arrange
        QuestAssignment assignment = completedEasyQuestFor(MemberRank.ELITE);

        // Act
        int reward = calculator.calculateExperienceReward(assignment);

        // Assert
        assertThat(reward).isEqualTo(130);
    }

    @Test
    void should_add_fixed_50_xp_boost_when_legendary_quest_completed_by_novice() {
        // Arrange
        QuestAssignment assignment = completedLegendaryQuestFor(MemberRank.NOVICE);

        //Act
        int reward = calculator.calculateExperienceReward(assignment);

        // Assert
        assertThat(reward).isEqualTo(150);
    }

    @Test
    void should_throw_business_exception_when_quest_is_not_completed() {
        // Arrange
        QuestAssignment assignment = notCompletedQuestFor(MemberRank.NOVICE, QuestDifficulty.EASY);

        assertThatThrownBy(() -> calculator.calculateExperienceReward(assignment))
            .isInstanceOf(QuestNotCompletedException.class);

    }
}
