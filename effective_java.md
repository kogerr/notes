#Effective Java
### Effectiveness and Performance
- sok a karbantartási munka, jobb a clean kód, ami nem romilik el máshol
- a teljesítményt hatékony kóddal érjük el, mint mellékhatás
- de a teljesítmény-gyilkos megoldást kerüljük
- letiszult APIt ne módosítsuk teljesítményért
- mindig kimérni, miből használ a sokat, ha optimalizálás előtt
- csúnya megoldást lokalizálni, elzárni, ne épüljön be, ki lehessen dobni

## Creating and Destroying Objects
### Static Factory Method - Instead of Constructors
- a klienst példányhoz juttatja constructor helyett vagy mellett
- nem egyenlő a factory patternnel
- pl Boolean.valueOf(string)
- szemben a konstruktorokkal van neve, könnyű olvasni
- nem kell mindíg új példányt visszaadnia (boolean, singleton, immutable, cache)
- instance-control :point_up:
- visszatérhet altípussal/speciális implementációt: emptyList, stb
- null check.. validálás
- "hátrányok":
  - calasses woth only static factories cannot be subclassed
  - not readily distinguishable from other static methods

### Builder - If Many Constructor Parameters
- lehet default value-kat, opcionális paramétereknél
- Telescoping Constructor Pattern helyett
- alternatíva a JavaBeans:
  - default no-arg constructor és setterek
  - de inkonzisztens lehet
  - nem lehet csekkolni invariánsokat
  - mutable
- példában: 
  - `Builder` az osztályon belül
  - Builderben megvannak ugyanazok a mezők, csak default értékekkel
  - buildert ad konstruktornak a `.build()`
- lehet ellenőrizni az invariánsokat

### Unnecessary Objects - Avoid
