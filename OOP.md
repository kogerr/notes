# Object-Oriented Principles

Mi a jelentősége
- valóságot szeretné modellezni, ez volt régen a tendencia tényleg
- nagyobb kódbázisnál jobb tagolni

Régen __functional decomposition__:
- lépésekre bont
- a lépéseket mégtovább
- a változással nem tud megkűzdeni

a megoldás: __Modularity__ - hogyan bontsuk modulokra
- alacsony lesz a modulok közötti kohézió
- kihat minden a többi modulra, mert szoros köztük a kapcsolat
- minden módosítás bug-okhoz vezet

__OOP__:
- letolni a felelősséget alacsonyabb szintre, objektumnak
- több fajta objektummal tudok kommunikálni, nem kell tudni, milyen

Ki a legfiatalabb? (példa)
- funkcionálisan: funkció végigmegy mindenkin
- OOP: ember objektum
  - ami tudja a korát, és össze tudja hasonlítani magát mással
  - sorokra bontva döntsék el maguk

Tanfolyam példa: egyenként megmondani mindenkinek, vagy kifüggeszteni

Object:
- from Abstract to Concrete:
  - __Conceptual level__: set of responsibilities
  - __Specification level__: set of methods
  - __Implementation level__: code and data

### Design Approach
- Ad-Hoc design: kerüljük, de nehéz vele együtt dolgozni
- szabály alapú: pl AInál: kell egy logika, ami megfelel a szabályoknak
- esemény alapú rendszer: pl weboldal
- Responsibility-driven design:
  - delegálja a vezérlést
  - koordunálja az eseményeket
  - kevés üzenet, viszont okos object-ek
- Data-Driven:
  - buta object-ek (info hordozók)
  - alacsony szintű üzenetek
  - kontrollerek

### Software Quality
- mennyire felel meg a specifikációnak az, amit csináltunk
- mennyire robosztus, mennyire tud reagálni nem várt eseményekre
- mennyire bővíthető
- karbantarthatóság. ha jön egy új fejlesztő, mennyire tud bekapcsolódni
(bus factor: hány embert üthet el a busz ahhoz, hogy menjen tovább)
- reusability
- compatibility
- perforamance: ha jó a kód, könnyen tudjuk majd igazítani a teljesítményt is a végén

## OOP alapelvek
- Encapsulation (egyégbe zárás) and Information hiding
- Absztrakció
- Öröklés
- Többarcúság (Polymorphism)

### Encapsulation
- zárjuk össze az adatokat a műveletekkel

Information hiding:
- nem csak annyi, hogy privát attribútumok és accessor-ok!
- belső koncepciókat is rejtsük el! hogy hogyan használjuk az adatokat (methods)
- védelmet is jelent: privát adattagokat nem tudják kívülről módosítani
- _redundáns_ adatok közti kapcsolat?

### Abstraction
- információkat elrejtünk, elfelejtünk
- hogy ne legyen túl bonyolult / irreleváns
- elkerülni a duplikációt
- nem korlátozott csoportját adja lehetséges viselkedéseknek
- stabilak a rendszerben

### Inheritence
- legerősebb kapcsolat két osztály között: mindent látnak egymásból
- _whitebox reuse_: belelát a dobozba
- ha változik az ősosztály, valószínűleg a származtatottat is kell változtatni
- kódismétlés ellen
- _is-a_ relationship: modellezésben nagyon jó
- bővithető az eredeti osztály leszármazottakkal

### Polymorphism (Többarcúság)
Több típusa van:
- function overload (de általában nem ezt értjük alatta)
- generikus programozás: pl `List` ugyanúgy viselkedik (kb.) attól függetlenül,
milyen osztály objektumait tartalmazza
- OO: function override: a leszármazott fölüldefinál egy method-ot

### Possible Relationships
először erősebbek:
- Inheritance
- Implementation
- Composition: ő hozza létre, ő szünteti meg
- Aggregation: nem tudom létre hozni, de ha kapok egyet, elvagyok vele
- Association: nem is biztos, hogy ki van töltve (tudom a számát, fel tudom hívni)
- Dependency: ha egyik osztályt módosítom, lehet, hogy kell a másikat is

### Inheritance vs Composition
Composition over inheritance
- szeretjük a gyengébb kapcsolatokat, minél lazább kapcsolat legyen
- izolálni a változásokat (ne kelljen megváltoztatni a másikat)
- viszont kompozíció változhat dinamikusan: öröklés statikus
  - lehet, hogy másik implementáció kerül oda (?)
  - nem látni a kódból, mi van az interface mögött (?)
- kompozíció: _has-a_ nem _is-a_: ha változtatni szeretnénk, könnyebb, dinamikusan
- hátrány:
  - plusz klompexitás
  - nem mindig megfelelő dizájn

### Coupling and Cohesion
Coupling:
- milyen szoros a kapcsolat két osztály között
- ha módosítom az egyik osztályt, mennyire valószínű, hogy módosítani kell a másikat
- kód duplikáció nem jó! nem látni, hogy máshol is kellett volna változtatni

Cohesion:
- mennyire kötődnek össze egy modul elemei

Ha erős a kohézió, általában laza a kapcsolat

## SOLID

### Single Reponsility Principle
- méretkorlátot ad az osztálynak
- egy mondatban el lehessen mondani, mi a dolga
- egy osztálynak egyetlen egy oka legyen a változásra
- pl ha egy class olvassa az adatbázist és kommunikál a felhasználóval, nem jó

### Open-Closed Principle
- a szoftver-entitások (osztályok) legyenek (amennyire lehet)
  - nyitottak a bővítésre
  - zártak a módosításra
- mert a módosítás hatással van az osztályt használó kliensekre
- olyan legyen, hogy bővíteni tudjuk leszármaztatással vagy kompozícióval
- megoldás: absztrakciók (lehetséges viselkedések csoportja)

### Liskov Substitution Principle
-  az ősosztály helyettesíthető kell legyen a leszármazott osztályokkal
- feltétele:
  - method signature-ök meg kell legyenek
  - prekondíciók (elvárások a paraméterként átadott adatokkal szemben):
csak erősíteni lehet. amilyen paraméterekkel meghívhattuk azt a method-ot,
a leszármazott osztálynak is jó kell legyen (pl null)
  - posztkondícioók: a visszaadott eredmény kompatibilis kell legyen (pl pozitív érték)
  - invariánsok: a method nem módosíthat bizonyos értékeket
  - nem változtathatja a belső állapotot úgy, hogy az megzavarja az ősosztályt
- ha nem, az kiszámíthatatlan viselkedéshez vezet
- visszautasított attribútum rossz jel

### Interface Segregation Principle
- ne kényszerítsünk senkire függőséget
- inkább szerep alapú (Role) interface legyen, mint header
- jobb ha megvalósítunk 2-3 interface-t mintha egy nagyot implementálunk
- másrészt a dependenciákat újra is kell compile-olni

### Dependency Inversion Principle
- a magas szintű modulok nem kéne függjenek az alacsony szintű moduloktól,
mindkettőnek abstrakcióktól kéne
- Abstractions should not depend on details. Details should depend on abstractions
- nem akarjuk hogy változáskor implementációs kérdés miatt át kelljen írni
- jobb betenni egy absztrakciós szintet (interface) a kettő közé, védve őket
- de hol fogjuk példányosítani az osztályokat ezekhez az interface-ekhez?
- _Inversion of Control container_ (?)

## 3
### Law of Demeter
- rejtett függőséget kerüljük
- ne legyen olyan, hogy elkérünk egy osztálytól egy osztályt és annak hívjuk
egy methodját
- hanem legyen a köztes osztálynak egy methodja, ami megcsinálja a kliens helyettesíthető
- "principle of least knowledge"

> Each unit should have only limited knowledge about other units: only units
"closely" related to the current unit.
> Each unit should only talk to its friends; don't talk to strangers.
> Only talk to your immediate friends.

__példa__: Client Class > Person > Department.getManager()

megoldott példa: `Application` > `Document` > `Paragraph.printLines()`

### Tell, don't ask Principle
- összekötjük az adatokat a hozzájuk tartozó metódusokkal
- az összefüggő dolgok legyenek ugyanabban a komponensben
- ha kétszer egymás után hívja valaminek a methodjait, lehet, hogy nem jó
- mondjuk meg az objektumnak, mit csináljon,
- nem: kérjük el az adatokat és azon módítsunk, műveleteket végzünk
- __példa__: négyszög kiíratása, oldalak lekérésével, ahelyett, hogy kiiratnánk
az objektummal

### Immutable and Value Objects
- Immutable:
  - nem megváltoztatható belső állapotú
  - rögtön szálbiztossá válik
  - egyszerű megérteni, nagyobb biztonság
  - hátránya: a módosításhoz másolni kell (memóriát igényel, __pl.__ String)
- Value Object:
  - értéket tárol
  - egyenlőséget nem referencia, hanem az értékek alapján vizsgáljuk
  - immutable tulajdonsággal illene, hogy rendelkezzenek
  - __például__: pénz, dátum

Megoldott példa:
- private final változók
- final class-ok
- változóként tárolt class-ok is immutable-ök, vagy másolatok: Moon, Date
- listáknak másolatát kapni, adni:
```java
new ArrayList<T>(list)
// ...
return Collections.unmodifiableList(moons); // vagy új listát
```
- paramétereket is finallé

### Design by Contract
- kinek a hibája, ha valami nem működik? a felelősség van, hogy két szék közé esik
- View the relationship between class and its clients as formal agreement
  - minden metódusra állapítsunk meg prekondíciókat, posztkondícioókat:
  - ellenőrizzük: ha ezt kapja, akkor ezt a választ kell adja (olyan paramétereket)
  - betartjuk-e az invariánsokat, amik nem változtathatóak a method lefutása alatta
- így könnyen elkapjuk a téves működést, megtaláljuk a bugokat
- többlet munka, de nagy előnnyel jár
- kliens felelőssége, hogy olyan adatokat adjon, amikkel ez a method tud működni,
prekondíciókat teljesítse
- Supplier kötelességei:
  - visszatérési érték
  - invariánsokat betartsa, ne okozzon mellékhatást
  - posztkondíciókat ezáltal teljesítse
- öröklésnél: ha ősosztály teljesíti a feltételeket, a leszármazottnak is

### KISS & YAGNI
- Keep It Simple Stupid
  - úgy a legjobb a rendszer, ha egyszerű, de csak annyira simple amennyire kell
- You ain't gonna need it: (agilis)
  - ne csinálj olyan általánosítást, ami nem biztos, hogy később szerepet fog kapni
  - inkább tesztelésre szánjuk az időt

### Packaging
- komplexitás csökkentése
- magas kohézió, alacsony kötés (kevés dependencia a csomagok között)
- Common Closure Principle
  - SRP for packages (egy mondatban, egy oka legyen a változásra...)
  - inkább legyen karbantartható, mint újrafelhasználható
- Reuse-release equivalence principle
  - ha újrahaszálhatóvá teszünk valamit, olyan, mintha release-elnénk
  - mert sok helyen lesz függőségünk, sok helyen okozhat problémát
- Common reuse principle
  - ha újrahelhasználható osztályokat teszek egy csomagba, legyen az egész
csomag újrafelhasználható, csak olyan dolgok kerüljenek bele
  - _példa_ Utils-jellegű csomag
- Acyclic Dependencies Principle
  - nem jó a ciklikus függés csomagok között
  - megoldás: Dependence Inversion Principle - legyen kiszervezve a függőség
- Stable Dependencies Principle
  - jobb a stabilabb csomagoktól függeni, a lánc végén álljon az instabil
  - megállapíthatunk arányszámot: Instability: kimenő és bemenő függőségekből
- Stable Abstractions Principle
  - az a jó, ha az implementációs csomagok absztrakcióktól függenek