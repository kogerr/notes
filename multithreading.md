# Multithreading
motivation:
- better use of resources
  - multi-CPU
  - Blocking oprtation - nem áll a program, fut a többi szál, kihasználja gépet
- concurrent world - a világ működésének logikus leképezése
  - concurrent request
  - scheduled / batch (háttér) task - pl esti adatbázis optimalizáció
  - periodic updates (e.g., UI) - progress bar, óra

### Paralell Processing
- Multiprocessing - több exe fájl az oprendszer ütemezője váltogat
  - IPC: interprocess communication
- többszálú: egy process-nek több szála van, sokkal kevésbé különülnek el
  - shared heap, private stack: virtuális gépen fut minden program, saját
virtuális címtéren. ezen osztoznak a szálak. 

_stack_: minden szálnak sajátja: hívási láncok, paraméterek a stackre kerülnek,
lokális változók...

### Why Synchronize?
A programon belül szinkronizálni kell a memóriához való hozzáférést
- out-of-order execution: nem olyan sorrendben fognak lefutni, ahogy terveztük
- processzorok is trükköznek: cache-ek vannak, regiszterekbe kerülnek értékek...

#### Példa
- register gyorsabb, mint cahce és az gyorsabb mint a fő memória
- nincs olyan, hogy egy adatnak egy helye van
- chace-ek kommunikációja: MESI
  - konzisztens: Shared
  - be kell olvasni: Invalid
  - még nem terjedt el a rendszerben: Modified
  - éppen lockoltam és én fogom csak írni: Exclusive
- a processzor adatot ír, és utána átállít egy flag-et
- ami nem a regiszterben és a cache-ben van, azt lassú elérni
- Speculative Execution: megpróbálja kitatlálni, mi lesz a művelet eredménye
  - ha megjön a memóriából az adat, kiderül, hogy jó volt-e a branch prediction
  - regiszterekből van backup páros, az alapján csinálja
- késve érkezhet meg az, hogy módosítva lett az érték
- a memória kontroller viszaad 1-es flaget - azt hiszi a másik CPU h jól számolt

### The Problem and the Solution
a baj: shared mutable data -> szinkronizálni kell. ez biztosít:
- rendezést - műveletek sorrendje
- visibility - látható lesz-e egy másik szálban

hatásai:
- helyi változtatásokat ki kell nyomni a közösbe
- cache-ben, regiszterben tárolt értékekért is frissíteni kell a közösből

### Java Memory Model
a memoria modellek a processzorok ígéretei. mik a hatásai a memória-műveleteknek
- a set of rules and guarantees on memory access
- CPU specific
- a JVMnek a 'write once, run everywhere'-hez szükséges
- a JVM a processzortól függetlenül milyen garanciákat vállal

#### Ordering: Happens-before
- részleges rendezés
- meg tudjuk mondani, h bizonyos műveletek hatása látható lesz másik szálakban

szabályok/garanciák:
- program order: egy szálon belül lehet, h a processzor és a compiler
átrendezi a műveletek sorrendjét, de szemantikai változás nincs. abban a
sorrendben látszódnak végrehajtódni, ahogy leírtuk
- `synchronized`: kölcsönös kizárás, lock (method/block)
  - sima sync. methodnál a this object lock-járól van szó
  - statikus sync. methodnál a class lockjáról
  - sync. blockbani írások egyszerre látszódnak végrehajtódni külvilág számára
- `volatile`: nem cserélhető fel változók írásának/olvasásának sorrendje
- `Thread.start()`: a szál belsejében látszik az, ami indítása előtt volt
- Thread termination: amit szál csinált, committolódik a memóriába mikor leáll
- `Thread.interrupt()`: interrupt előtti műveleteket látja a szál akkor
- Construcion/finalization: constructor-ban végrehajtott műveletek legkésőbb
finalize-kor láthatóvá válnak a memóriában
- Transitivity: ha a megelőzi B-t, és Bé Cét, akkor A is Cét

a program különböző hardware-eken máshogy futhat, ha nincs jól szinkronizálva

### Basic Support for Multithreading
Basic types and keywords:

Types
- Runnable
- Thread
- ThreadGroup

Keywords
- synchronized
- volatile előtti írásokat nem lehet utána, olvasásokat utána nem lehet elétenni
- final

### Thrad Life-Cycle
- létrehozáskor még nem történik semmi
- Ready-to-run: semmi sem akadályozza, csak nincs processzor, ami végrehajtsa
- futó állapotból kikerülhet blokkoló múvelettel is: sleep/lockra vár/join/IO/..

### Thread API
- `start()`
- `run()`
- `interrupt()`, `isInterruped()`, `interrupted()` - üzenetet küldeni, vizsgálni
- `setName("threadName")` érdemes beállítani nevet adminisztratív célból
- `setDaemon(true/false)`: háttérszál, nem akadályozza meg a VM kilépését
- `yield()`: szál átadja a processzort valaki másnak, jelzés a java runtime felé
- `setPriority()`: be lehet állítani szál prioritását. OS-enként más kicsit

preEmptive multitask: nem a processeken és szálakon múlik hogy min dolgozik CPU

### ThreadGroup
össze lehet fogni szálakat - nem szoktuk használni. meg lehet adni közösen:
- prioritást
- exceptionHandlert
- daemon

### Synchronized
- minden object-nek van monitora - lock, ami:
- mutual exclusion-t (mutex) valósít meg
- atomicity - ami a syncronizált block belsejében történik, egyszerre látszódik

#### `wait()`/`notify()`/`notifyAll()`
- szálak jeleznek egymásnak
- csak synchronizált blockból lehet meghívni
- arra az objekumra kell szinkronizálni, amin magát a methodot is hívjuk
- wait() elküld aludni, míg nem kap:
  - üzenetet (notify) - simán fölébred
  - interruptot - exception-nel ébred
- amíg feltételre várunk (`while()`-ban) addig alszunk
- ébredéskor ellenőrizzük feltételt újra
- aki szól: beállítja a feltétel változót, aztán `notifyAll()`

#### Synchronized vs This
mikor használunk szinkronizált metódust vagy mikor használjuk a thist
- `this`-szel baj: a mi object-ünkön más is hívhat wait/notify-t, lock-olhatja
- megoldás: ne használjunk szinkronizált method-ot, inkább:
- vezessünk be egy final mezőt: guard object (sima `Object`)
- erre szinkronizálunk
- minden szinkronizált blokkunkat és wait/notify-t erre az objektumra építjük
- ez az object private, úgyhogy más nem tudja használni

### Volatile
- nem lehet fölcserélgetni a műveleteket (Ordering, _happens-before_)
- atomicity: kicsit synchronized block szerűen működik

#### példa Boolean
- thread nem látja static booleant, amíg nem volatile
- oka lehet:
  - optimalizációkor kiesett a check
  - processzor cache-eli

#### példa Long
- nemvárt értéket kap
- kettes komplemens ábrázolás
- 64 bites long
- 32 bites JVM-en két számítás lesz
- ezt is megoldja a volatile

### Final
lehet mező/attribute, method parameter, class vagy függvény is `final`
- (final static belevéshető a bytecode-ba)
- immutable:
  - create new instance instead of modification
  - persistent data structures

- InvariantDemo: inmutable class gyorsabb, mert nem várnak egymásra accessorok
- ha sok szál verseng egy object-ért, lassabb, mintha kizárjuk a szinkronizációt
- ha erős a versengés a lock-okért, lehet, h immutable object-tel gyorsabb

### Problems and Antipatterns
- Random bahaviour: ha hibás a szinkronizáció látszólag láthatatlan működésének
- Deadlock: két vagy több szál vál valamire, de senki nem tud tovább lépni
- Livelock: mindkét ember kilép a másik oldalra
- Performace bottlenecks: olyat írunk, amitől nagyon lassú lesz
- Unsafe publication: objektumok nem kellően inicializálva láthatóvá válnak más
threadek számára. Félig kész object-ek láthatóak valahol.

### Random Behaviour
Nem szabad olyan objektumra szinkronizálni, aminek az értéke aztán megváltozhat
- mert nem a mező nevére szinkronizálunk, hanem a referenciára

### Race Condition
Errors due to bad synchronization:
- lost updates
- inconsistent state
- lost notification: pl wait-notify-nál ne legyen előbb a notify mint a wait
- double-checked locking

példa inkonzisztens állapotra:
- egyik szál módosítja az x és y értékét, a másik olvassa
- rosszul van szinkronizálva - csak `y` szinkronizált
- kiolvashatja az egyik módosított értéket és a másikból a régit
- ilyenkor vagy előjön a baj, vagy nem, nem kiszámítható

példa PlusPlusDemo - lost updates:
- elindít két szálat, a szálak szólnak ha végeztek és beállítják `Semaphore`-t
- 50 millióig kéne elszámoljon kétszer, de csak 50mil körüli az érték
- a szálak látják, hogy hogy áll az érték indulás előtt, és a végén visszaadják
- `volatile`-lal 100x lassabb lett, de nem lett sokkal jobb eredmény
- `volatile`-lal mindig elmennek a memóriába egy értékért, de megint felülírják
- guard Object a megoldás
- a synchronized block csak akkor engedi el, amikor időszelete lejár

példa Double-checked locking:
- singleton object-nél újra megnézi, hogy azóta se hozta-e létre senki
- biztonságos kellene, hogy legyen
- de lehet, hogy constructor futása közben jön a másik szál
- megoldás: szinkronizált method, vagy nem lazy, vagy Pugh-féle Singleton-holder

pugh:
- `static class Holder` benne `static final` field
- de a belső osztály csak `getInstance()`-re töltődik be, szóval igazából Lazy
- jó, ha valamilyen konfigurációkat be kell tölteni mielőtt létrehozzuk
- ha csak statikus mező, mert ott kell legyen, amikor az osztály betóltődik
- így viszont meírhatunk előtte statikus konfigurációs mezőket
- villám gyors és biztos is

### Performance Bottlenecks
- synchronization costs: szerverekben szokott előjönni...
- hogyha túl sokáig van nálad egy monitor, feltartja a többi ember kódját
  - ne legyenek lassú hívások...
  - lehetőleg ne legyen sleep/wait/block...
  - pl loggolást is ha lehet, ne synchronized block-ban
(lehet, hogy másik szerver végzi a loggolást)
- holdiong too many locks - ha sok lock van, érdemes sorrendet előírni
- too strict locking (readers vs writers) - ha olvasó a többi olvasót is pl.

### Unsafe Publication
előbb adja át magát (`this`) az event managernek, minthogy lefutna a constructor
