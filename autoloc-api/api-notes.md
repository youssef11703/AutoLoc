# Notes — Atelier 5 : API REST et Swagger

## A. Endpoints

| Contrôleur | Verbe & Route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| ContratController | GET /api/contrats | findAll() | 200 | - |
| ContratController | GET /api/contrats/{id} | findById(id) | 200 | 404 |
| ContratController | POST /api/contrats | create() | 201 | 400 |
| ContratController | PUT /api/contrats/{id} | update() | 200 | 404, 400 |
| ContratController | DELETE /api/contrats/{id} | deleteById() | 204 | 404 |
| PaiementController | GET /api/paiements | findAll() | 200 | - |
| PaiementController | GET /api/paiements/{id} | findById(id) | 200 | 404 |

*(Ajouter les autres contrôleurs ici)*

## B. Annotations Jackson

| Association | Annotation | Justification |
|---|---|---|
| Contrat ↔ Paiement | `@JsonManagedReference` / `@JsonBackReference` | C'est une composition. On veut voir les paiements depuis le Contrat, mais pas le Contrat depuis le Paiement. |
| Agence → Vehicle | `@JsonIgnore` sur la liste dans Agence | Un véhicule peut exister sans qu'on ait besoin de voir la liste complète de son agence dans le JSON. |

**Différence :** `@JsonBackReference` est utilisé pour couper une boucle bidirectionnelle (le champ est ignoré à la sérialisation mais accepté à la désérialisation). `@JsonIgnore` ignore complètement le champ (ni lecture ni écriture).

## C. Postman et Swagger
- **Postman** : Permet de tester, de sauvegarder des scénarios et d'automatiser des assertions (ex: scénario complet de création d'un contrat + cascade sur les paiements).
- **Swagger UI** : Génère automatiquement une documentation interactive à partir du code. Permet de tester sans installer Postman, avec les schémas JSON attendus prédéfinis.

## D. Anomalies SonarQube
1. **S1118** : `Utility classes should not have public constructors` (si applicable).
2. **S1172** : Unused method parameters (si tu as des paramètres inutilisés).
   *(Corriger et noter ici)*

## E. Questions de compréhension

1. **Quel composant reçoit toutes les requêtes ?** Le `DispatcherServlet`. Il utilise le `HandlerMapping` pour trouver la bonne méthode de contrôleur en fonction de l'URL et du verbe HTTP.
2. **Pourquoi dépendre de `IContratService` et pas de `ContratRepository` ?** Pour respecter la séparation des couches (MVC). Le contrôleur ne doit pas connaître la base de données, il délègue la logique métier au service.
3. **Pourquoi POST renvoie 201 et DELETE 204 ?** 201 (Created) indique qu'une ressource a été créée. 204 (No Content) indique que la suppression a réussi mais qu'il n'y a rien à retourner. Une ressource absente donne 404 (Not Found) car c'est une erreur du client (mauvaise URL), pas du serveur (qui n'a pas planté).
4. **Pourquoi PaiementController n'expose-t-il que des GET ?** Car un paiement ne se crée/supprime que via son Contrat (cascade). Un POST renvoie 405 (Method Not Allowed).
5. **Risques de l'exposition directe des entités ?**
    - Fuite de données sensibles (ex: mot de passe, champs internes).
    - Couplage fort entre la base de données et l'API (si on renomme une colonne, l'API casse).
    - Boucles infinies (réglées ici par Jackson, mais fragiles).
    - `Contrat.paiements` utilise `@JsonManagedReference` car on veut créer les paiements imbriqués. `Agence.vehicules` utilise `@JsonIgnore` car on ne veut pas charger tous les véhicules quand on consulte une agence (problème de performance).