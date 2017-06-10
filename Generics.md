# Introduction
- típusértéket kapnak osztályok/methodok/interface--ek
- Nem mondjuk meg, mi a típusa, mintha sablon lenne
- pl. Collectionök
- visszafelé kompatibilissé akarták tenni, ami szívás
- oka: ne kelljen cast-olni és type safety (foreach-hez is kell, autoboxing)
- foreach - ConcurrentModificationException-höz vezethet (számláló)
- varargs: változó számú argunentum használata
  - a háttérben ez is tömböket használ
  - ha egy változónak őstípusa egy másik, akkor az Array-nek is az az Array
  - Object Array-ként fogja értelmezni, de egy Array Object is lehetne
- Autoboxing: `.valueOf()` és `.primitiveTypeValue()`

# Basic Syntax
- 2 féle:
  - Generic Types - collection kap típus paramétert (diamond operator java7 óta)
  - Gereric Methods - generikus metódusak is lehet adni típus paramétert
- egybetűs, nagybetűvel
- konstruktornál már nem kell megadni (deklaráláskor)
- compiler elvégzi a típusellenőrzést is
- explicit type parameter, pl: `Arrays.<Serializable>asList("...", ...)`

# Type System
- primitív típus / referencia típus (egyenlőség érték / referencia szerint)
- interface-ben minden publikus és csak konstansokat lehet definiálni
- `super()` - legközelebbit találja meg
- Array-t interface-ből is lehet

Gyakorlat
- nem lehet generikus array, vagy egyből cast-oljuk `(T[])`, vagy egyenként
- vagy constructor-ban megadom a típust és `Array.newInstance(myType, 100)`
- `<? extends BaseType>` - addAll methodnál pl. 
- Futásidőben konkrét típust fog jelölni

Hierarchia:
```java
List<? extends Object>
List<? extends Number>
List<Number>;List<Integer>;List<Double>
```

- ~~`List<Object> a = new List<Integer>`~~ nem fordul le!
- Raw referencián keresztül bele lehet tenni más típust
- `<?>`-lel RunTime hiba: nem lehet meghívni Stringgel
- `<? extends T>` listába csak nullt lehet tenni, mert nem tudja a típust
- nem lehet ősosztály Comparator-rel sort-olni:
```java
public void sort(Comparator<? super T> comparator) {
	Arrays.sort(myArray, 0, myElementCount, comparator);
}
```
- _contravariant subtyping_
- __Get Put Principle__ wildcard szabály: 
  - ha metódust fogok hívni az adott objektumon, (mint mondjuk a Comparatoron T típusú értékeket hasonlítok össze) oda `? super T` kell, hogy elfogadja
  - ha pedig a visszatérési értékét használom, akkor `extends`, pl `addAll` - a bejövő de ismeretlen típusú listáról azt várom el, hogy altípusa legyen

- akkor használunk `<T>` ha valamilyen típusegyezést akarunk előírni
- a `<?>` azt üzeni, hogy bármilyen listát betehetünk
- wildcard <?> korlátozások:
  - instatnce creation
  - ősökben
  - generikus metódusokban

# Runtime
- visszafelé kompatibilitás adja a nehézségek nagy részét
- nem lehet lista típusát elkérni
- checkedSet, checkedList - runtime ellenőrzi, hogy tényleg megfelelnek-e típusnak
- compiler eldobja a típus paramétereket, (a `T`-ket pedig) lecseréli az alkalmazott megszorításra (<.. extends...>), ha nincs, akkor Objectre. ha több is volt, a legbalabbra
- erasure után lehet, hogy ütköznek a signature-ök, hogyha csak a típus más:
```java
addAll(GenericList<Double> numbers)
addAll(GenericList<Integer> numbers)
```
- nem lehet ugyanazt az interface-t sem többször megvalósítani más típussal
- bridge flag : régi kódnak csinál compiler bridge-et
(binary-ben `volatile` methodon)
- Reifiable types: can be fully represented at runtime:
  - primitives
  - non-parameterised types
  - raw and wildcard parameterised: `List`, `List<?>`
  - Arrays of reifiable types: `int[]`, `List<?>[]`
- no `instanceof T` vagy `instanceof List<T>`
- no instantiation of generic array
- nem tudunk exception-öket sem gyártani, amik típusparaméterezve vannak
- példa: nem Override volt, mert nem ugyanaz a signature-je
  - lehet castolni super class-ra, hogy annak a methodja hívódjon meg
- _Principle of indicent exposure_:
  - Never return an array of a non-reifiable type



