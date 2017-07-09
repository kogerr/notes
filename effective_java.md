# Effective Java
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
- reuse, ahol lehet, ahelyett, hogy ekvivalenset csinálnánk
- immutable-ből következik az újrafelhasználhatóság
- static facetory & object pools
- költséges vagy korlátozott erőforrások esetén szükséges (Calendar, DB...)
- ne: `String s = new String("stringette");` memóriában már amúgy is létrejön
- creating costly objects unnecessarily (Calendar, Date)
- avoiding recommended static factory methods (`Boolean.valueOf()`)

example isBabyBoomer:
- fölöslegesen létrehozott Date, Calendar object-ekvivalenset
- kirendezi static initializer blockba, így nem jön létre mindig újra

### Eliminate obsolete object references
example Stack:
- akaratlan referenciavisszatartás / loitering / Obsolete reference
- memóriából az törlődik, amire nincs referencia
- a tömb továbbra is fogja ezeket a referenciákat

- figyelni kell magunknak foglalt memóriára
  - solution: nulling out obsolete references
  - scope-ból kilépve megszűnik a referencia
  - statikusan létrehozott objektumok megmaradnak
- do not forget irrelevant elements in cache
  - `WeakHashMap(key)` - weak referenciát felszabadíthatja a GC
  - scheduled clean
- Callbacks:
  - deregister them or use weak keys
- UI-nál listener úgymaradhat

### Avoid Finalizers
- tipikusan: errőforrásoktól megszabadulás, handlerek, kapcsolatok lezárása...
- finalize nem tudni mikor fut le, ha egyáltalán helyesen lefut
- van értelme, biztonsági háló
- írjunk explicit termináló függvényeket, pl: `.close()`
  - meghívhatja kliens is

## Methods Common to All Objects
- amik az Object-ből jönnek
- `equals`, `HashCode`, `toString`, `clone`, `finalize`
  - megvan, h miket kell betartaniuk

### Equals
egy objektum alapból csakis önmagával egyenlő. 
jó a default viselkedés, ha:
- instances of the class are inherently unique, pl: Threadnél
- "logical equality" does not matter, e.g.: `Random`
- superclass already overriden it properly, e.g. Abstract-Set/List/Map
- class is private/package-private && equals not used (throw `AssertionError`) 

- value class-oknál fölül kell írni logical equivalence érdekében

Equals contract: equivalence relation (in Javadoc)
- Reflexive: `x.equals(x)`
- Symmetric: `x.equals(y)` <=> `y.equals(x)`
- Transitive: `x.equals(y) && y.equals(z)` => `x.equals(z)`
- Consistent: `x.equals(y)` once => always, unless state changes
- x.equals(null) always false, if `x!= null`

example ColorPoint
- `getClass` helyett `instanceof`-ot használ, null-t is szűri
- ha csak a saját osztályát fogadja el, nem felel meg Liskovnak
- ha színvak összehasonlítást végez, nem tranzitív

Recipe (recipe2: use Guava)
1. Use the `==` operator to self-check
2. Use `instanceof` to check correct type
3. Cast argument
4. Test equality of each significant field - nullt is / Guava / Objects.equals?
5. Check if: symmetric / consistent / transitivy
6. Override hashCode

### HashCode - always override if equals is overriden
example PhoneNumber Map
- hashMapen belül először hashCode alapján tájolódik, de aztán equals-szal keres
- ezért is járnak párban, különben nem működne hash-alapú Collection-ökben
- nem kötelező, de igyekeznek egyéni hashCode-ot írni

Contract:
- Consistent: ~ returns same value unless information used in equals() modified
- ha equals-szal egyenlő, legyen a hashCode-juk is
- de attól még, hogy nem equals, lehet azonos a hashCode-juk

1. Store some constant nonzero value; say 17, in an int variable called result.
2. For each significant field f in your object (each field taken into account by
the equals()), do the following
  1. Compute an int hashCode c for the field:
    - If the field is a boolean, compute c = (f ? 1 : 0).
    - If the field is a byte, char, short, or int, compute c = (int) f.
    - If the field is a long, compute c = (int) (f ^ (f >>> 32)).
    - If the field is a float, compute c = Float.floatToIntBits(f).
    - If the field is a double,compute long l = Double.doubleToLongBits(f),
c = (int)(l ^ (l >>> 32))
    - If the field is an object reference then equals( ) calls equals( ) for
this field. compute c = f.hashCode()
    - If the field is an array, treat it as if each element were a separate
field. That is, compute a hashCode for each significant element by applying
above rules to each element
  2. Combine the hashCode c computed in step 2.1 into result as follows:
`result = 37 * result + c;`
3. Return result.
4. Look at the resulting hashCode() and make sure that equal instances have
equal hash codes.

- legyen jó a szórása
- immutable object-ek hashCode-ja legyen cached (String is cache-eli)
- String lazy számolja ki

### toString - always override
hogyha logban kapod meg, hogy hol volt a baj, jól jön
- tipikus (calss név és fieldek értékei)
- Guava: Objects.helper
- jól meghatározott, formált toStiring - jó irni hozzá Javadoc-ot
- középút: majdnem specifikus. nincs megkötött, de tipikusan olyan, hogy... (?)
- concise but informative, easy to read
- ajánlott, hogy minden subclass override-olja
- legyen benne minden szignifikáns info
- formátumot dokumentálni
- provide programmatic access

### Exceptions
olvashatóságot, megbízhatóságot, karbantarthatóságot növel, ha jó
- csak különleges esetekre használjuk (ne while loop-hoz pl)

#### Use Checked Exceptions for Recoverable Conditions and Runtime Exceptions for
Programming Errors
Checked exceptions:
- amikor elkerülhetetlen, hogy megtörténik a hiba és helyre tud állni a caller
- a hívó vagy kapja el, vagy továbbítsa

Unchecked throwables:
- Errors
  - nagy családja a virtual machine errorok: out of memory, internal error...
  - resource deficiencies, invarian faliures, etc.
- Runtime exceptions
  - tipikusan programozási hibák
  - dobjunk, ha sérült a szerződés, pl NullPointerException-t
  - nem kéne ezeket elkapni, mert nem lehet lekezelni, pl bug, klienstől vmi...

#### Favour the Use of Standard Exceptions
- strive for reusability
- e.g.:
  - `IllegalArgumentException`
  - `NullPointerException`
  - `UnsupportedOperationException`

#### Throw Exceptions Appropriate to the Abstraction Level
- exception translation (akár checkedből unchecked)
- chaining (don't overuse!)

#### Document All Exceptions Thrown by Each Method
- checked: declare + document (throws + `@throws`)
- unchecked: document only `@throws`

#### Include Faliure-Capture Information in Detailed Messages

#### Strive for Failure Atomicity
- ne lépjen ki inkonzisztens állapotban

#### Don't Ignore Exceptions

### Consider Implementing Comparable
- class indicates that instances have natural ordering
- users of the class will be grateful
- other ordering can be used with Comparator

szabályok:
- szimmetria: sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
- trnazitivitás
- ajánlott, hogy ha compareTo egyenlőek, akkor equals

Guava: `ComparisonChain`

**Don't depend on the Thread Scheduler**

### Classes and Interfaces
#### Minimize the accessibility of classes and members
#### Minimize mutability
#### Composition over Inheritance
#### Interfaces over abstract classes
#### Class hierarchies over tagged classes (?)
#### Use enums insted of int constants
#### Use EnumSet instead of bit fields