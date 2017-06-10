# Java8 - 2

## Date API
- régi Date: nem voltak időzónák, mutable
- Calendar class - túl bonyolult lett, mutable, 0tól számol

__LocalDate, LocalTime!__

_LocalDate:_
- `.now()`			kiírja yyyy-MM-dd formában
- `.of(2017, 5, 9)`		szintén kiírja
- `.ofYearDay(2017, 159)`	

_LocalTime:_
- `.now()`
- `.of(13,11)`

_LocalDateTime:_
- `.now()`
- `.now(ZoneId.of("America/New_York"))`
- `.now(Clock.systemUTC)`
`ZonedDaateTime.now()`
immutable osztály, visszatérési értékként ad vissza olyat, pl h:
`.plusDays(int)`

## kép feldolgozás
a példában 3 osztály:
- Coordinate (x, y)
- Pixel
- PixelStore

URI = getResource

Buffered Image:
- ImageIO.read(file)
- `.getHeight`, `.getWidth`
- `.getRGB(x, y)

pixelek feldolgozása:
```java
int[] rgbArray = pixels
.stream().peek(p -> p.applySepia())
.mapToInt(p -> p.toInt()).toArray();
```
`BufferedImage.setRGB()`
`ImageIO.write()`
`.stream()` helyett `.paralellStream()` többszálú!
ehhez volatile-lá tett valamit

## további konkurencia kezeléses újdonságok
- counter:
  - AtomicLong - volt a java 5-ben
  - szinkronizált counter - még régabbi
  - LongAdder, LongAccumulator - az új!
- reflection
  - paraméterek neve eltűnik fordításkor
  - új fordítási parameter (eclipse: pref/compiler)
  - *Store information about method parameters*
- annotációkban is fejlődött
  - `@annotation` -nel lehet létrehozni sajátot (nem új)
  - most már több annotációt is lehet egyre tenni:
`@Repeatable(value = Filters.class)` (Filters a neve)
- javaScript futtató API: `nashorn` ScriptEngine

