# GuildKeeper - Projet final

**Nom :**
**Date :**
**Dépôt Git :**

> Ce fichier a deux rôles : la checklist ci-dessous sert de suivi pendant les 3 heures, la synthèse en fin de fichier est le livrable 5. Garder la synthèse sur une page maximum.

---

## Suivi des tâches

Le détail de chaque livrable est dans les slides du projet. Cette checklist ne reprend que la progression TDD des dividendes, où l'oubli d'un cas coûte des points, et les contrôles à passer avant le rendu.

### Livrables (cocher quand terminé)

- [X] Livrable 1 : suite de tests complète de `GuildFinanceService`
- [X] Livrable 2 : `finance.feature` et ses step definitions Cucumber
- [X] Livrable 3 : `distributeDividends` développé en TDD (détail ci-dessous)
- [X] Livrable 4 : rapport de couverture généré
- [ ] Livrable 5 : synthèse écrite ci-dessous

### Livrable 3 - Progression TDD des dividendes

Un cycle rouge -> vert -> refactor à chaque palier, chaque test écrit avant le code de production.

- [X] palier 1 : guilde vide -> répartition retournée vide, compte inchangé (test rouge imposé, à écrire en premier)
- [X] palier 2 : un seul membre -> il reçoit toute l'enveloppe, le compte est débité d'autant
- [X] palier 3 : deux membres de rangs différents -> parts au prorata des poids, reliquat laissé sur le compte
- [X] palier 4 : `@ParameterizedTest` sur `p` invalide (`0`, `-5`) -> `InvalidAmountException`, compte inchangé
- [ ] palier 5 : `p > 100`, un seul membre, solde `100`, `p = 200` -> `checkSolvency` renvoie `false` -> `InsufficientFundsException`, compte inchangé
- [ ] palier 6 : le solde ne devient jamais négatif

```text
J'ai remarqué que les codes nécessaires n'avait pas de sens, p est incohérent s'il dépasse 100% car c'est un pourcentage,
ducoup le palier 4 j'ai ajouté comme quoi p est invalide si p > 100.

Les paliers 5 et 6 deviennent inutiles.
```

### Contrôles avant rendu

- [X] `./mvnw test` et `npm test` verts
- [ ] `./mvnw test -Ptodo` vert : plus aucun message « Test à compléter » /////////////////// (4 restants rouges)
- [ ] `npm run test:todo` vert /////////////////// (4 restants rouges)
- [ ] couverture du module `finance` supérieure ou égale à 80 %
- [X] aucun test flaky : la suite passe aussi quand l'ordre des tests change
- [X] méthodes existantes de `GuildFinanceService` non modifiées (hors `distributeDividends`) //////////////////// (Une autre méthode ajoutée)

---

## Synthèse écrite (livrable 5, une page maximum)

Les tests de fonctions sont principalement unitaires. Ces tests sont essentiels pour assurer qu'une fonction fait bien
ce qu'elle doit faire.

Les tests bout-en-bout sont utiles pour vérifier que les fonctions marchent entre elles ainsi qu'avec les services
reliés.

Mockito permet essentiellement pour les tests unitaires de simuler des services externes pour l'exécution des tests,
ça simplifie largement l'architecture nécessaire à leur éxecution.

Les tests paramétrés permettent en un test écrit d'en executer plusieurs dans différentes configurations. Très pratique
lorsque les possibilités sont multiples.

### Niveau de couverture retenu

Couverture obtenue sur le module `finance` : 95 % pour les instructions et 88% pour les branches

Pourquoi ce niveau : quelles lignes ou branches restent non couvertes, et pourquoi c'est acceptable ou non.

C'est l'objet GuildAccount directement qui possède des conditions non testées. Actuellement c'est important de le tester
car le service ne prends pas la charge de la gestion de l'entité (Le CRUD), mais seulement les intéractions autour. La vrai
question a se poser c'est est-ce que ces branches conditionnels sont justifiées ici ? Ou devrait-on pouvoir gérer l'entité
via un manager qui lui sera testé.

### Choix de stratégie de test

- Unitaire contre bout-en-bout : ce qui est testé en isolation, ce qui passe par Cucumber, et pourquoi.
- Usage de Mockito : sur quelles dépendances, stub (`thenReturn`) ou mock (`verify`), et la raison.
- Paramétrage : quels cas regroupés en `@ParameterizedTest`, quelle source de données.
- Données de test : comment les comptes et les membres sont construits, comment le déterminisme est garanti.

### Problèmes rencontrés et solutions

- Problème : Livrable 3 avec les tests 5 et 6 inutiles dans ces conditions.
  - Solution: Le test 4 teste aussi que le % accepté par 'distributeDividends' ne dépasse pas 100%.
