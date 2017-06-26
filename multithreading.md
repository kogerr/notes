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
a baj: shared mutable date -> szinkronizálni kell. ez biztosít:
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
