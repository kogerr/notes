# Library Features
## Lambdas and Functional Interfaces
eclipse: QuickFix -> convert to lambda expression
lustaság

## Interface

default method: ha nem adjuk meg, ez van benne,
de nem kötelező implementálni

lehet static methodokat is

_diamond_ (többszörös) öröklés esetén meg kell adni,
hogy melyik interface method-ját használjuk, pl:
`LoggerFacade.super.log(level, message);`

# óra
stream: osztály, amivel funkcionális programozást lehet megvalósítani
Collections framework kiegészült ezzel a streammel
Optional: ne kelljen állandóan null ellenőrzéseket
value object: bizonyos értéket hordoz, jobb, ha immutable

# cleancode
rossz kód: sok benne a bug, nehéz dolgozni vele, változtatni, fejleszteni, lassabb
későbbi magaddal és másokkal cseszel ki
operation alatt drága javítani


