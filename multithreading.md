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
##### Errors due to bad synchronization:
- lost updates
- inconsistent state
- lost notification: pl wait-notify-nál ne legyen előbb a notify mint a wait
- double-checked locking

##### példa inkonzisztens állapotra:
- egyik szál módosítja az x és y értékét, a másik olvassa
- rosszul van szinkronizálva - csak `y` szinkronizált
- kiolvashatja az egyik módosított értéket és a másikból a régit
- ilyenkor vagy előjön a baj, vagy nem, nem kiszámítható

##### példa PlusPlusDemo - lost updates:
- elindít két szálat, a szálak szólnak ha végeztek és beállítják `Semaphore`-t
- 50 millióig kéne elszámoljon kétszer, de csak 50mil körüli az érték
- a szálak látják, hogy hogy áll az érték indulás előtt, és a végén visszaadják
- `volatile`-lal 100x lassabb lett, de nem lett sokkal jobb eredmény
- `volatile`-lal mindig elmennek a memóriába egy értékért, de megint felülírják
- guard Object a megoldás
- a synchronized block csak akkor engedi el, amikor időszelete lejár

##### példa Double-checked locking:
- singleton object-nél újra megnézi, hogy azóta se hozta-e létre senki
- biztonságos kellene, hogy legyen
- de lehet, hogy constructor futása közben jön a másik szál
- megoldás: szinkronizált method, vagy nem lazy, vagy Pugh-féle Singleton-holder

##### pugh:
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
- holding too many locks - ha sok lock van, érdemes sorrendet előírni
- too strict locking (readers vs writers) - ha olvasó a többi olvasót is pl.

### Unsafe Publication
előbb adja át magát (`this`) pl event managernek, minthogy lefutna a constructor

Safe publication
- volatile field - a mezőt csak akkor hívja, ha a mögötte lévő object elkészült
- field guarded by lock - szinkronizált blockban
- static initializer - pl Pugh (bár nem block) addig nem kerül rá referencia
- final field of a properly constructed object (és constructor végigfut)
- thread safe collection
- thread safe library classes (`AtomicReference`, `BlockingQueue`, ...)

### Starting Thread Subclass in constructor
kerülendő constructorban elindítani egy szálat (önmagát)
- leszármazott osztály megpróbálja kétszer elindítani a szálat, nem lehet
- már fut a szál, pedig még nincs beállítva field...

### Deadlock
- Circular wait
- Philosopher demo
- SleepingWithLockHeldDemo

example Philosopher
- egy idő után deadlock
- `jconsole`, `jps`, `jstack -l szám`, `kill -QUIT szám`, `Ctrl-Break`

### Exercise
exercise Rendezvous:
- feladvány: két szál egyszerre végezzen, egyik várja meg a másikat
- megoldás: static guard object és static isSomeoneWaiting boolean
- `guard.wait()` és `guard.notifyAll()`
- szinkronizált block miatt nem is kell volatile-nak lennie a flagnek

filozófus:
- asszimetria bevitelével meg lehet törni: egyik fordítva nyúl a villához
- előírja, hogy mindenki a magasabb sorszámút fogja meg először

exercise SleepingWithLockHeldDemo
- synchronized get method-ban várakozik, így lock-olja és nem engedi set-et
- `sleep` nem engedi el a lock-ot, a `wait` igen

### Thread Termination
- vége, amikor run véget ér
- `stop()` és `destroy()` deprecated, nem szabad használni
- exit condition - be lehet vezetni egy kilépési feltételt
- interrupt()
- poison pill - külön érték, ami azt jelzi, hogy most kell befejezni

#### Exit Condition:
- volatile flag jelzi, hogy működni kell
- run methodban folyamatosan vizsgálja értékét

#### The Interrupted Status
- minden szálhoz tartozik egy beépített interrupted flag
- be lehet állítani aaz `interrupt()` hívásával az objecten
- ha a szál bemegy egy sleep/wait/más methodba, `InterruptedException`t dob erre
- nem vész el, akkor se, ha az interrupt előbb jött (szemben `notify`-jal)
- detection:
  - `InterruptedException` (clears flag)
  - `Thread.interrupted()` (clears flag)
  - `aThread.isInterrupted()` (unchanged)

#### Passzív szál (alatt azt érti: alszik, amíg egy feltétel nem teljesül)
- interrupt hatására fölébredhet `InterruptedException`-nel
- visszaadhat egy különleges visszatérési értéket
- saját magán ismét bebillentheti interrupt flaget

#### Interrupting a Busy Thread:
lehet a beépített flag-et ellenőrizni
- `while(!Thread.interrupted())`
- `while(!Thread.currentThread().interrupted())`

#### Propagating the Interrupted Status:
- ha ki akarsz lépni, ne kapd el az exception-t, szülő megkapja
- ha muszály elkapni:
  - return special result
  - return normal result, but interrupt thread, hogy a szülő is lássa a flaget
- példa:
  - boolean-nal jegyzi, hogy volt interrupt, de megy tovább
  - a végén `interrupt()`-ot hív magára
  - így a szülő le tudja kérdezni, és látja hogy volt interrupt

#### The Poison Pill
Collection-be, amit földolgoz, tesz egy elemet, amire megszakad
- egy speciális érték, ami jelzi, hogy be kell fejezni a földolgozást

#### Dirty Tricks
néhány művelet nem úgy van megtervezve, hogy félbe lehessen szakítani
- ha `java.io` hálózati socketből olvasna, de blokkol, nem jön adat:
  - csak úgy tud kitörni, ha bezárja a socketet
  - kap a szál, aki olvasna egy SocketException-t
- `java.nio`-ban is be lehet zárni a csatornát, de van InterruptibleChannel
- Asynchronous I/O - értesítenek, amikor elkészül egy művelet - wakeUp method...

## Library Support
### Thread Confinement
a szálhoz kötjük az állapotot
- Thread-confined values don't need synchronization:
  - stack confinement: local variables (tipikusan hívási paraméterek)
  - Thread-specific value: `ThreadLocal` (like a Map keyed by Threads) *?*

ThreadLocalDemo

### Collections
ha több szálból akarunk elérni egy Collectiont:
- ne lehessen módosítás iterálás közben
- ne sérülhessen az adatszerkezet mert többen módosítják egyszerre
- lehetne szinkronizálni rá, de kényelmetlen lenne
- vannak, amik eleve ilyenek voltak, pl Vector
- `Collections.synchroized...()` - add/get/remove... methodok synchronizáltak

##### ConcurrentEventVenue1:
- `ConcurrentModificationException`
- mert a módosításszámláló változott iterálás közben
- a `HashSet`(`AbstractCollection`) `toString()`-je iterál
- megoldás: `Collection.synchronizedSet(new HashSet<Person>())`
- rögtön becsomagoljuk, hogy ne lehessen elérni synchronizáció nélkül
- `synchronizedSet` sok methodjában van egy `synchronized (mutex)` block

##### ConcurrentEventVenue2:
- TreeMap-hez hozzáadásakor lefut az `addAll` iterátora fut ugyanebbe a hibába
- eközben valaki módosítja a tartalmát
- megoldás: syncronized block arra a collectionre, amit hozzáad
```java
synchronized (attendees) {
    sortedPeople = new TreeSet<Person>(attendees);
}
```

#### Concurrent Collections
jobban föl vannak készítve a párhuzamos működésre, mint synchronized
- processzorokon alacsony szintű atomi műveletek teszik lehetővé
- compare-and-set: 
  - kiolvasnak
  - összehasonlítják várt értékkel
  - feltételesen hajtják végre a módosítást
- Fail-fast helyett fail-safe iterators: végigmegy, max nem tükrözi módosítást
- Better performance than simple locking
- pl: `CopyOnWriteArrayList` iteráláskor módosítást másolaton hajtja végre
- `CopyOnWriteArraySet`
- `ConcurrentSkipListSet`:
  - kicsit fára hasonlít és *heudisztikus* (?)
  - előnye: compare-and-exchange műveletekkel hatékonyan lockolás nélkül tud...
- `ConcurrentHashMap`, `ConcurrentSkipListMap`

##### ConcurrentMap:
sok check-then-modify atomi művelet van benne(alacsony szintű CPU műveletekkel):
- `putIfAbsent(key, value)`
- `replace(key, newValue)`
- `replace(key, expectedOldValue, newValue)`
- `remove(key, expectedValue)`

```java
private final Set<Person> myAttendees =
            Collections.newSetFromMap(new ConcurrentHashMap<Person, Boolean>());
```

### Queue
Producer/Consumer - munkákat kapnak és végrehajtják. Queue átmeneti buffer
- head/tail
- FIFO/LIFO(Verem/Stack)/priority-based/delayed(lejárati dátumig nem jönnek)
- simle or double-ended (Deque)

`java.util.Queue`
- Extends `Collection`
- Not all implementations thread-safe (e.g. `LinkedList`, `ArrayDeque` are not)
- capacity: bounded/unbounded - korlátlan/korlátozott a mérete (block/exception)

true/exception	| true/false/null
--------------- | ---------------
add(element)	| offer(element)
remove()	| poll()
element()	| peek()

#### BlockigQueue
```java
put()
offer(element. timeout, timeUnit)
take()
poll(timeout, timeUnit)
drainTo(collection) // adok neki egy collection-t, amibe üríti
```
TransferQueue: speciális blocking Queue: producer értesül, h consumer kivett

#### Other Queues
- Deque
  - BlockingDeque
  - `Collections.asLifoQueue(deque)` Stack-et csinál Deque-ből
- PriorityQueue
- DelayQueue
- SynchronousQueue: tesztelésnél. BlockingQueue, kapacitása 0. randevú szerű

### Atomic Operations
Check-then-modify:
- incrementing counters (PlusPlusDemo) nem atomi, hanem három műveletből áll.
- taking ownership of resource if not owned - szintén atomi művelettel
- support in CPUs: compare-and-swap(CAS) (intel: LOCK CMPXCHG)
- also backed by Java libraries

Types for Atomic Operations
- Atomic Integer/Long - számlálóknak, pl `getAndIncrement`
- AtomicBoolean - atomi reference, amiben a CAS booleanre van állítva
- AtomicReference (+Array)
  - compareAndSet(expectedValue, newValue)
  - getAndSet(newValue)
- AtomicReference/Integer/LongFieldUpdater: 1 osztály mezőjéhez ad CAS műveletet

AtomicInteger/Long
[]()			|
----------------------- | -------------------
get()			| set(newValue)
getAndSet(newValue)	| setAndGet(newValue)
incrementAndGet()	| getAndIncrement()
decrementAndGet()	| getAndDecrement()
addAndGet(delta)	| getAndAdd(delta)

AtomicReference Extensions
- AtomicStampedReference: lehet hozzá fűzni számlálót
- AtomicMarkableReference: +boolean flag

### Synchronization Facilities

#### Semaphore
kölcsönös kizárást megvalósító eszköz (vonatoknál jelzőzászló).
- a legprimitívebb egy flag: binary semaphore
- a javában: maximális érték, amiből elvehetek, visszatölthetek
- pl egyszerre tizen férhetnek hozzá:
  - mindenki nyom a semaphore-on olyat, hogy `acquire(n)`t, aki hozzá akar férni
  - akár tönbb permitet is kérhet
  - ha nincs elég, blokkol, várakozni kell. viszont interruptolható
(synchronized blockkal szemben)
- `acquireUninterruptibly(n)`
- `tryAcquire()` - lekérdezés, nem blockkol, visszatér booleannal, h sikerült-e
- `tryAcquire(timeout, timeUnit)` - boolean: lejárt-e vagy megkapta
- `release(n)` - figyelni kell, hogy mennyit adok vissza
- `drainPermits()` - el lehet venni az összeset (shutdown környékén)
- `availablePermits()` - lekérdezni, hány darab van

#### CountDownLatch
Kezdeti megadott értékről indul
- `countDown()`
- `await()`. `await(timeout, timeUnit)`
- nem lehet visszaállítani

#### CyclicBarrier
ez újraindítható, meg lehet adni egy Runnable-t, ami lefut, amikor befejeződött
- Optional Runnable (barrier action) to ipdate shared/merge results of subtasks
- `await(timeout, timeUnit)`
- `getParties()` - hány résztvevője van
- `getNumberWaiting()` - ebből még hány van hátra, hogy elérjük a 0-át

#### Phaser
a CyclicBarrierhez hasonló, de: parties may register/unregister at any time

#### Explicit Locks
a synchronized helyett van. nehezebb használni, de rugalmasabb

##### Synchronized:
- egyszerűsége csak az előnye
- ha belefutok synchronized blockba belefutva nehéz megnézni, hogy foglalt-e
- nem tudok timeout-tal várni
- nem tudom elkülöníteni az írásokat és az olvasásokat (kizárhatják egymást)
- nincs garancia, h abban a sorrendben kapják meg a szálak a blockba belépést,
ahogy jelentkeztek. OS dönt (nem baj)

##### `java.util.concurrent.locks`
Lock (interface)
- `lock()`, `lockInterruptibly()` - amivel el lehet kérni egy lockot
- `tryLock()` / `tryLock(timeout, timeUnit)` - megpróbálja elkérni, boolean
- `unlock()` - feltétel nélkül elengedi a lockot
- `condition()` - kicsit hasonó wait/notify-hoz. egy lockhoz többet is lehet ?

ReadWriteLock - ezt terjeszti ki funkcionálisan
- Two separate Lock instances
- Does not extend Lock
- elkülöníti olvasókat és írókat

Condition - hasonló a wait/notify-hoz
- `signal()` / `signalAll()`
- `await(timeout, timeUnit)`, await- Uninterruptibly/Nanos/Until

##### Lock Implementations
- ReentrantLock
  - mint a synchronized block
  - újra be lehet lépni: ahogy egy szál synchronized methodból másikba...
  - ha újra és újra megfogom a lockját, számlál, hogy milyen mélyen van lockolva
- ReentrantReadWriteLock - el lehet különíteni irókat/olvasókat
- StampedLock
  - lehet kérni csak írási/olvasási lockot, át lehet állítani
  - writing: excluisive access (no other reading/writing thread)
  - reading: non-exclusive access (other readers allowed, writers not)
  - optimistic reading: unlocked, for quick reads

ReadWriteDemo
- synchronized methodokon keresztül / ReadWriteLock-kal
- mindegyiket megszakítjuk és megszámoljuk mennyi r/w művelet sikerült
- explicit lock-ot mindig föl kell oldani `finally`ben
- lock egyetlen final object, ettől elkérni külön a r/w lockot
- eredmény: kétszer annyi írót és olvasót szolgált ki az explicit lock

### The Executor Framework
ne kelljen kézzel szálakat indítani
- executor igazából ThreadPool
- Pool: előre létrehozott erőforrások halmaza, szükség szerint használjuk föl
- executor service használatával indítjuk a szálakat
- executorral vagy fix vagy dinamikus számú szálat hozatunk létre
- mi csak a feladatokat adjuk oda neki
- előnye:
  - tudja szabályozni, hány szál fut egyszerre
  - időkorlátot tud
  - feladatok lelőhetőek lehetnek

Main Types
- Executor (interface)
  - `execute(runnableTask)`
- ExecutorService
  - `Callable<T>` - megmondhatom, milyen visszatérési értékkel bírjon
  - `submit(...)` - ezekkel a methodokkal lehet bedobni ExecutorService-be
  - `invokeAll(...)` - több feladatot tesz be, Future-ök listáját adja vissza
  - `invokeAny(...)` - több feladatot tesz be, az első eredményét adja vissza
  - `Future<T>` - jövőbeli eredmény, lehet kérdezni, hogy kész van-e már
- ThreadFactory - object, ExecutorService hívja (szál prioritása, neve, stb)

##### ExecutorService
- ThreadPoolExecutor: hozzá lehet adni queue-t feladatoknak (FIFO/prioritásos..)
- ScheduledExecutorService: időzítést lehet megadni
- ForkJoinPool: automatikus feladatszétosztás/párhuzamosítás
- Creation
  - Factory methods in `Executors` - simple
  - Direct instantiation - complete control
  
example ForkJoinMax:
- `RecursiveTask extends ForkJoinTask`
- maximumot keres. ha túl nagy a tömb szétvágja két részre
- `left.fork()` - ExecutorService elkezd ezen az új szálon dolgozni
- `Math.max(right.compute(), left.join())`
- feladatokról beszélünk csak, nem szálakról
- ForkJoin kétszer olyan gyors volt

Shutdown support
- methods:
  - `shutdown()` - ne kezdjen bele újba
  - `shutDownNow()` - interruptot is küld
  - `awaitTermination()` - megvárni, míg ténylegesen befejeződik a végrehajtás
  - `isShutdown()`, `isTerminated`
- rejection policy: `RejectionExecutionHandler` - ha túl van terhelve pool
  - abort - exceptiont dob, ha új feladatot kap
  - caller runs - hívó álljon be, és csinálja meg a feladatot
  - discard - nem csinál semmit új feladattal
  - discard oldest (not for PriorityQueue)

`Executors` Factory Methods For
- fixed number of threads (even single threaded)
- dynamically changing number of threads (cached) - min/max
- scheduled

CompletionService
- Producers submit tasks to work queue
- Workers take tasks from work qurur
- Consumert take completed tasks from result queue

### What more is out there?
- Java 8 paralell streams
- Scala: filter/lambda/forEach szerűen beadhatók funkciók
- Actor framework-ök: aszinkron a működés...
- Software Transactional Memory (STM) - újraszámol, ha módosult az érték

### Double-Ended Queue
- ha egy szálnak nincs munkája, lophat a másik szál végéről munkát
- megoldható, hogy terhelés szétoszlik a rendszerben
