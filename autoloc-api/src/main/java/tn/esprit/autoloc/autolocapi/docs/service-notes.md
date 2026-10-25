# Notes — Atelier 4 : IoC, DI et couche Service

## A. Modes d'injection comparés

| Critère | Constructeur | Attribut |
|---|---|---|
| Champ `final` possible ? | ✅ Oui | ❌ Non |
| Dépendance visible dans la signature ? | ✅ Oui | ❌ Non |
| Utilisable avec `new` hors conteneur ? | ✅ Oui (avec dépendance fournie) | ❌ Non (dép. null) |
| Signalé par SonarQube ? | ❌ Non | ✅ Oui (S6813) |

**Conclusion** : injection par constructeur adoptée partout dans AutoLoc.

## B. Services et dépendances

| Service | Dépendance injectée | Mode | Justification |
|---|---|---|---|
| ContratServiceImpl | IContratRepository | Constructeur | CRUD + garde sur montant |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Lecture seule (cascade via Contrat) |
| VehicleServiceImpl | IVehicleRepository | Constructeur | CRUD + tarif non négatif |
| AgenceServiceImpl | IAgenceRepository | Constructeur | CRUD + nom obligatoire |
| ClientServiceImpl | IClientRepository | Constructeur | CRUD + email obligatoire |
| EmployeeServiceImpl | IEmployeeRepository | Constructeur | CRUD + rôle obligatoire |
| EquipementServiceImpl | IEquipementRepository | Constructeur | CRUD + libellé obligatoire |
| ReservationServiceImpl | IReservationRepository | Constructeur | CRUD + dates cohérentes |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | CRUD + dates cohérentes |

## C. Messages d'erreur du conteneur

### Message 1 — NoSuchBeanDefinitionException
- **Cause** : `@Service` manquant ou classe hors du package scanné.
- **Correction** : ajouter `@Service` et vérifier le package.

### Message 2 — NoUniqueBeanDefinitionException
- **Cause** : deux beans du même type (emailNotificateur, smsNotificateur).
- **Corrections** :
    1. `@Primary` sur `EmailNotificateur` (candidat par défaut).
    2. `@Qualifier("smsNotificateur")` sur le paramètre du constructeur.

### Message 3 — Cycle de dépendances
- **Cause** : `ClientServiceImpl` ↔ `ReservationServiceImpl`.
- **Pourquoi le constructeur rend le cycle impossible** : la construction de A nécessite B entièrement construit, qui nécessite A → boucle infinie détectée immédiatement.
- **Piste** : extraire une 3ᵉ classe (`ClientReservationService`) ou passer par des IDs.

## D. Anomalies SonarQube relevées

| Règle | Description | Correction |
|---|---|---|
| S6813 | Injection par attribut déconseillée | Utiliser l'injection par constructeur |
| S112 | Lever une exception générique | Utiliser une exception métier dédiée |
| S106  | `System.out.println` | Utiliser un logger SLF4J |

## E. Questions de compréhension

1. **Où est le `new ContratServiceImpl` ?** Nulle part dans notre code : c'est le conteneur Spring qui l'instancie au démarrage (scan + réflexion Java).
2. **Pourquoi dépendre de `IContratService` et non `ContratServiceImpl` ?** Pour respecter le principe d'inversion de dépendances : on peut changer d'implémentation (mock en test, autre implémentation) sans toucher au contrôleur.
3. **Pourquoi un singleton doit-il être sans état ?** Il est partagé par tous les threads. Exemple de bug : stocker `contratCourant` dans un attribut → deux requêtes simultanées se marcheraient dessus.
4. **Pourquoi `update` charge puis recopie ?** Passer l'objet reçu directement à `save` déclencherait un `merge` global et, avec `orphanRemoval`, viderait la collection `paiements` non fournie.
5. **Différences** :
    - `@Component` : annotation sur **classe** détectée par scan.
    - `@Bean` : annotation sur **méthode** dans une classe `@Configuration`, pour des objets qu'on ne peut pas annoter.
    - `@Primary` : posé sur la **classe**, désigne le candidat par défaut.
    - `@Qualifier` : posé sur le **point d'injection**, choisit un bean précis.
    - `IPaiementService` est en lecture seule car un paiement ne peut exister que via son contrat (cascade ALL + orphanRemoval).