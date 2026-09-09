package fr.dev.sensei.guild.keeper.experience;

public class LevelCalculator {
    public int calculateLevel(int experiencePoints) {
        if(experiencePoints < 0){
            throw new IllegalArgumentException("Experience points must be positive");
        }

        return experiencePoints/100 + 1;
    }
}
