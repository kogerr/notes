# Generics
Use:
- type check
- instead of casts
- easier to read generic algorithms

## Generic Types
- __Simple Box Class__: No way to verify what type is stored.
- __Generic Box__:
  - `<T>` type parameter after class name
  - can use `T` class instead of Object

#### Type Parameter Naming
single uppercase letter

` ` | meaning
--- | -------
`E` | element
`K` | key
`N` | Number
`T` | Type
`V` | Value

#### Invoking and Instantiating a Generic Type
- generic type invocation: by passing a type argument
  - _type parameter_: `Foo<T>`
  - _type argument_: `Foo<String>`
- invocation: _parameterised type_

Diamond operator, multiple type parameters...

Parameterized type as type parameter:
`OrderedPair<String, Box<Integer>>`

### Raw Types
- when type argument is omitted
- lot in legacy code
- for compatibility: can assign raw type to a parameterised type:
```java
Box rawBox = new Box();           // rawBox is a raw type of Box<T>
Box<Integer> intBox = rawBox;     // warning: unchecked conversion
```
also
```java
Box<String> stringBox = new Box<>();
Box rawBox = stringBox;
rawBox.set(8);  // warning: unchecked invocation to set(T)
```

#### Unchecked Error Messages
- can happen when using an older API that operates on raw types
- to see all "unchecked" warnings, recompile with `-Xlint:unchecked`
- To completely disable unchecked warnings, use the `-Xlint:-unchecked` flag.
- The `@SuppressWarnings("unchecked")` annotation suppresses unchecked warnings.

## Generic Methods
- parameter's scope is limited to the method
- type parameter inside angle brackets before the method's return type
- _type inference_: no need to explicitly provide type
(`Util.<Integer, String>compare(p1, p2);`)

## Bounded Type Parameters
- to restrict the types that can be used as type arguments
- bounded type parameters allow you to invoke methods defined in the bounds
- Multiple Bounds: `<T extends B1 & B2 & B3>`

### Generic Methods and Bounded Type Parameters
key to the implementation of generic algorithms, eg: `<T extends Comparable<T>>`

## Generics, Inheritance, and Subtypes
can assign subtypes to types, but `Box<Integer>` not subtype of `Box<Number>`

## Type Inference
- compiler looks at method invocations & declarations to determine type argument
- eg. 2nd argument being passed to the pick method is of type Serializable:
```java
static <T> T pick(T a1, T a2) { return a2; }
Serializable s = pick("d", new ArrayList<String>());
```

#### Type Inference and Generic Methods
compiler infers the type parameters of a generic method call

#### Type Inference and Instantiation of Generic Classes
constructors can be generic (`<T> MyClass(T t)`)

#### Target Types
- data type that the compiler expects depending on where the expression appears
- `List<String> l = Collections.emptyList();` - target type: `List<String>`
- SE7: `processStringList(Collections.<String>emptyList());`
- SE8: the compiler infers that the type argument T has a value of String:
  `processStringList(Collections.emptyList());` compiles

## Wildcards
used as:
- the type of a parameter
- field
- local variable
- even return type (not advised)

not used as:
- type argument
- generic method invocation
- generic class instance creation
- supertype

- __Upper Bounded Wildcards__: `<? extends SuperType>`
- __Unbounded Wildcards__: `<?>`
  - for methods only using functionality provided in the Object class
  - using methods in the generic class (List.size or List.clear)
- __Lower Bounded Wildcards__: `<? super A>` (cannot both upper and lower bound)
- __Wildcards and Subtyping__
  - ![Subtyping example](https://docs.oracle.com/javase/tutorial/figures/java/generics-wildcardSubtyping.gif)

### Wildcard Capture and Helper Methods
Helper:
```java
void foo(List<?> i) {
        fooHelper(i);
    }

    // Helper method created so that the wildcard can be captured
    // through type inference.
    private <T> void fooHelper(List<T> l) {
        l.set(0, l.get(0));
    }
```

## Type Erasure
- bridging methods
- non-reifiable types
