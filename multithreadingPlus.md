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
