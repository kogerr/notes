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
- a JVMnek a 'writeonce, run everywhere'-hez szükséges