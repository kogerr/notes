# Refactoring
### introduction
~:
- easier to understand
- cheaper to modify
- no change in observable behaviour

why?
- improve the design of the software
- olvashatóbb legyen
- könnyebb bugokat megtalálni
- gyorsabban lehessen fejleszteni
- ne legyen duplikáció

van, hogy nehéz:
- nagy adatbázis modelleket
- publikált interface-eket
- architektúrális változások (pl biztonságos legyen)

### Bad Smells in Code
- Duplicate Code - struktúra ismétlődése is
- hosszú methodok
- nagy osztály (felelősségek száma)
- paraméter lista: 3 még belefér
- divergent/szerteágazó módosítás: pl felhasználói igényre - enum switch
- shotgun surgery (sörétes): egy kis módosítást sok helyen lekövetni
- fature envy: ha más osztály methodjait/attr. használja mindig egy osztály
- data clumps: együtt járó adattagok, pl koordináták
- magic constants: egy szám, ami csak oda van téve, és nem tudjuk, mit jelent
- primitiv megszállotság: pl Stringet föl lehet használni mindenre - nem OOP
- switch ne legyen magasszintű logikában
- párhuzamos öröklési hierarchia: duplikáció (divergens ?)
- lazy class: már nincs is rá szükség, vagy nincs adattagokhoz művelet
- speculatív általánosítás: yagnit
- temporális attribútumok: null értéket kerülni
- hívási láncok: talán buildernél megengedett, de amúgy demeterszabály
- túl sok közvetítő: felesleges mediator pl, csak delegál egy osztály
- inappropriate intimacy: olyan mint irigység
- alternate classes with different inerfaces: pl ha 2 fejlesztő írta
- incomplete library class: hiányoznak megfelelő metódusai Date helyett Calendar
- Data class-ok nem kapnak felelősséget
- Refused Bequest: nem akarunk implementálni az ősosztály egy hasznos methodját
- comment: el kell gondolkozni, nem lehet-e anélkül érthetővé tenni

### Tesztelés
A legjobb ha van autómatikus JUnit tesztünk, és minden lépés után futtajuk

a teszt, mint kliens

##
### Composing Methods
#### extract method
- pl ha nincsenek egy absztrakciós szinten, itt sysout
- adunk neki nevet, akkor átgondoljuk, mi a felelőssége
- nehézsége függ attól, hogy használ-e, változtat-e helyi változón
#### Inline method
- az előző ellentéte
- figyelni kell, ha van örökléssel kapcsolatos probléma, akkor nem megoldható
#### Inline Temp
- mégse csináljunk egy temporális változót
- figyeljünk, hogy mindenhol helyettesítsük be
#### Replace Temp with Query
- hasonlo az előzőhöz, csak számítással helyettesítjük be (nem hívással)
- akkor is, ha számítást végez, nem akkora baj
- betesz változó helyére egy void method-ot, ami összeszorozza (számítást kisze)
#### Explaining Variable
- ez az ellentéte. hogy érthető legyen. érdemes final-re állítani őket
- jó nevet kell adni neki
#### Split Temporary Variable
- ha két felelőssége is van egy temp változónak
- régen még a memóriával kellett spórolni
#### Remove Assignments to Parameters
- inkább bevezetni egy új változót, és azon változtatni
- nem biztos, hogy mindenki tudja, hogy változik-e az eredeti..
#### Replace Method w/ Method Object
- kiszervezni sokváltozós methodot egy másik classba
- átadva önmagát? (this)
- nagy a kohézió, felelősslg ki van szervezve, teljesítmény nem para
#### Substitute Algorithm
- tenni egy lépést hátra és lecserélni egy egyszerűbbre

### Moving Features Between Objects
#### Move Method
- a vele együttjáró adatokat azt nem akarjuk-e a methoddal együtt mozgatni
- de általában másik osztályhoz tartozó adatokon dolgozik
- meghagyjuk az eredetit? ha igen, delegálás
- figyelni kell, nincs-e öröklés, nincs-e felüldefiniálva
#### Move Field
- figyelni, pl ha `protected` a láthatósága, használja-e a leszármazott
- encapsulate field: getterrel és setterrel érjük el
#### Extract Class
- ha nagyon sok attribútuma van, vannak, amik szorosan összekapcsolódnak
- pl person és telefonszám
- ha változik az osztály felelőssége, figyeljünk, hogy átnevezzük
- szükséges lehet, hogy összekössük a két osztályt egy attribútummal
#### Inline Class
- Egy lusta osztályt be lehet helyettesíteni
#### Hide Delegate
- hogy a Demeter szabályt be tudjuk tartani, dependenciát eltüntessük
#### Remove Middle Man
- az előző fordítva
#### Introduce Foreign Method
- kiszervezve egy utility class-ba, w/ instance of the server class as argument
- cél elkerülni az ismétlődést
#### Intorduce Local Extension
- örökölve megoldani
- de akkor másik osztályt kell példányosítani és lehet, hogy változik az ős

### Organizing Data
#### Self Encapsulate Field
- Getterrel-setterrel elérni az attribútumokat: csk úgy jó, ha nincs mindenhez
#### Replace Data Value with Object
- primitive obsession: van, hogy csak valami primitívet használnak....
- helyetadni a műveleteknek
#### Change Value to Reference
- megengedjük, hogy ne legyenek immutable-ök
#### Change Value to Reference
- ha rájövünk ha csak valami értéket tárol
#### Replace Array with Object
- tömbben különböző értékek.. hozzunk létre rá egy osztályt (koordináták pl)
#### Replace Magic Number with Symbolic Constant
- elnevezni a konstanst a jelentése után
#### Encapsulate Field
- külső szemlélő számára elrejtjük a publikus attribútumokat
- listákat ne adjuk ki
#### Encapsulate Collection
- ha csak olvasható nézetet akarunk egy kollekcióra, nem adhatunk rá gettert
- kell rá írni add/remove methodokat
#### Replace Type Code with Type / Subclass
- típuskódokat kerülni kell, hogyha módosíthatják a működést
- enumba vagy származott osztályokba kiszervezni
#### Replace Type Code with Strategy
- hogyha dinamikusan változhat
#### Replace Subclass with Fields
- van, hogy a s ubclassok túl nagy bonyodalmat keltenek, nincs rájuk szükség
- egyszerűbb, hogyha ősosztályban eltároljuk valahogy

### Óra
- high level ~: a struktúrát változtatjuk, toolok nem csinálják meg helyettünk
- _Ctrl+S_ minden final
- gondoljuk meg, melyik irányba van a hivatkozás

### Simplifying Conditional Expressions
#### Decompose Conditional
- kiszervezni a feltételt egy methodba
- adjunk neki beszédes nevet
#### Consolidate Conditional Expressions
- ha ugyanaz a kimenete több feltételnek, kiszervezhetjük egybe
#### Consolidate Duplicate Conditional Expressions
- ha mindkét ág végén ott van ugyanaz, tegyük a conditionalön kívülre
#### Replace Conditional with Polymorphism
- pl switchcase hgelyett..
- figyelni kell, hogy ne használjuk az `instanceof` kulcsszót
- előfordul, hogy a láthatóságon is kell változtatni
#### Introduce Null Object
- Replace the null value with a null object
- Motivation: You have repeated checks for a null value
- Note: the null objects can be shared
- ha gyakori a null ellenőrzés, bevezethetünk 1 spec. objektumot (Null Customer)
- ami tartalmazhatja azokat a speciális eseteket, amikor null checkre lenne szükség

### Making Method Calls Simpler
jó interfacehez (APIhoz)
#### Rename Method
- jó neveket használjunk (nehéz)
#### Add/Remove Parameters
- paraméterlista: jobb letávolítani
- eclipse: change method signature
#### Separate Query from Modifier
- ha van visszatérési értéke: Query - akkor ne legyen mellékhatása
- többszálúságnál van atomi kivétel
#### Parameterize Method
- a method nevébe, vagy a paraméterbe legyen föltüntetve valami?
- jobb szokott lenni, hogyha parameterben
- ha flag boolean, akkor jobb két külön methodot létrehozni
#### Replace Parameter with Explicit Methods
- külön method legyen inkább
#### Preserve Whole Object
- ha egy object több adattagját átadjuk, akkor inkább adjuk át az egészet
- egy hátránya lehet: néha dependenciát vezet be
#### Replace Parameter with Method
- a paramétert helyettesítsük methoddal - tipikusan flag típusú
#### Introduce ParameterObject
- ha nagyon sok paraméterünk van, megfontolhatjuk, hogy plusz p.o.-t vezetünk be
- pl start, end helyett range
#### Remove Setter Method
- a setterektől szeretünk megszabadulni, jobb ha az objektumon belül történik
- a field should be set at creation time
- create immutable
#### Hide Method
- a láthatóságra is figyelni kell, hogy szükség van-e rá
- privátra állítani
#### Replace Constructor with Static Factory Method
- more than simple construction
- tudunk nevet adni neki
- nem muszáj visszatérnie egy adott típussal
#### Replace Error Code with Exception
- régi berögződés visszatérni 0val
- inkább Runtime Exception
#### Replace Exception with Test
- inkább próbáljuk elkerülni az exceptionöket

### Dealing with Generalization
#### Pull Up Field
Ha két leszármazott osztályban is szerepel ugyanaz, fölhozzzuk az ősosztályba
#### Push Down Field
refused bequest problémája áll fenn: nincs szüksége egy subclassnak valamire
#### Pull Up Method
de hogyha fölhozunk egy methodot, a hozzá tartozó attribútumokat/methodokat is
#### Push Down Method
szintén attribútumokkal együtt
#### Pull Up Constructor
ha ugyanaz a rész ismétlődik az alosztályokban, kerüljük el a kódismétlést
#### Extract Subclass
- Egy osztályból ki tudunk bontani egy ős/al osztályt
- akkor érdemes megteni, ha lesz testvérosztálya
#### Extract Subclass
ősosztályba szervezve elkerüljük a kódismétlést
#### Extract Interface
privát vagy protected methodokat nem fogja
#### Collapse Hierarchy
- két öröklési viszonyban álló osztályból egyet csinálunk
- fölösleges leszármaztatás/öröklés van, vagy áttérünk pl Strategy mintára
#### Replace Inheritance with Delegation
a legtöbb design pattern: ne öröklést használjunk, hanem composition-t
#### Replace Delegation with Inheritance
erre azért sokkal kevesebb lehetőség van, a kompozíció megfelelőbb szokott lenni

## Refactoring in Eclipse
Action | keybinding
--------------- | ---------------
Show key assist | `Ctrl + Shift + L`
Organize imports | `Ctrl + Shift + O`
Format code 	| `Ctrl + Shift + F`
Open type 	| `Ctrl + Shift + T`
Type Hierarchy 	| `Ctrl + T`
Go to 		| `Ctrl + click`
References 	| `Ctrl + Shift + G`
Refactorings 	| `Alt + Shift + T`
Source 		| `Alt + Shift + S`
Move 		| `Alt + Shift + V`
Abstract Method | `Alt + Shift + M`
Intline 	| `Alt + Shift + I`
Extract Local Variable | `Alt + Shift + L`
