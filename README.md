# Nocturne
Projet de jeu de déduction sociale à rôles cachés pour Minecraft (NeoForge 26.1).
L'objectif est de proposer une expérience de jeu rapide et automatisée où chaque joueur reçoit un rôle unique avec des capacités spécifiques durant une phase de nuit.

## Fonctionnalités
* **Parties rapides et automatisées :** Une seule nuit, un seul jour, un seul vote. Le mod gère entièrement le timer, la distribution aléatoire, le réveil, les actions des rôles et la résolution finale.
* **Rôles variés :** Jouez avec de multiples rôles ayant chacun leurs propres pouvoirs et conditions de victoire.
* **Menus interactifs :** Configuration simple via des interfaces graphiques en jeu pour composer manuellement le deck de la partie ou utiliser des presets préconfigurés selon le nombre de joueurs.
* **Maître du jeu :** Le joueur qui initie la partie garde le contrôle sur la révélation finale des rôles et de l'historique après les votes.

## Commandes
Le mod se gère via la commande principale `/nocturne` (nécessite les permissions opérateur) :
* `/nocturne start` : Lance la partie avec les joueurs connectés et vous désigne Maître du Jeu.
* `/nocturne stop` : Annule la partie en cours de force.
* `/nocturne pause` : Met en pause ou reprend le timer du jeu.
* `/nocturne skip` : Vote pour passer le temps restant et déclencher immédiatement le vote final.
* `/nocturne compo` : Ouvre l'interface de sélection manuelle des rôles.
* `/nocturne composet` : Ouvre l'interface des compositions rapides (presets) pour équilibrer le deck.

## Setup technique
L'architecture est orientée POO pour faciliter l'ajout de rôles complexes à l'avenir (duh, c'est du java). 
Elle utilise un système d'héritage (classe abstraite `Role`), des registres personnalisés via `DeferredRegister` pour les rôles, et une gestion de partie centralisée via `GameSession` et `GameBoard`.

1. Préparer l'environnement (sous IntelliJ) :

`./gradlew genIntellijRuns`

2. Lancer le client :

`./gradlew runClient`

## Licence

Ce projet est distribué sous licence GPLv3.
