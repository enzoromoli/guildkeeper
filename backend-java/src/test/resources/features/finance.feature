# language: fr
Fonctionnalité: Distribution de butin de la guilde

  Contexte:
    Soit un système de guilde

  Règle: La distribution de loot par la guilde n'est réussie que si elle à la balance nécessaire est supérieur au loot demandé et que le loot demandé n'est pas négatif ou nul

    Scénario: Distribution réussie par la guilde
      Soit "Aleka" guilde déjà existante avec une balance de 130
      Et "Aleka" essaye de distribuer 110 de loot
      Alors la balance de la guilde "Aleka" sera de 20

    Scénario: Distribution échouée pour cause de balance trop faible
      Soit "Aleka" guilde déjà existante avec une balance de 130
      Et "Aleka" essaye de distribuer 150 de loot
      Alors la distribution est rejeté pour cause de balance trop basse
