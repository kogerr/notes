# Unified Modeling Language (UML)

[IBM UML Basics](https://www.ibm.com/developerworks/rational/library/769.html)

## Use Case Diagram
- illustrates a unit of functionality
- to visualize the functional requirements of a system:
  - (human) actors' relationships to processes
  - relationships among use cases

### Notation
- lines between stickpersons and ovals (with labels)
- used to communicate high-level functions of the system and the system's scope
- even external sources
- shows what the system doesn't do

## Class Diagram
- static structure of the system: how entities relate to each other
- can describe logical (RL) classes / implementation class (diagram)

### Notation
Sections: name / attributes / methods

Relationships:
- inheritance: completed triangle
- association:
  - both know about it: simple lines
  - only one class knows: open arrow head

## Sequence Diagram
- shows flow for a use case or part of a use cases
- shows calls between different objects

### Notation
- vertical: calls in time order
- horizontal: object instances to which the messages are sent
- top: objects
- message: line with an open arrowhead
- important message: dotted line with arrowhead pointing back at original class

## Statechart diagram
- states of a class and transitions
- for classes with 3+ states

### Notation
Elements:
- starting point: solid circle
- transition: line with open/closed arrowhead
- state: rounded rectangle
- decision point: open circle
- termination point: circle with solid circle inside
  
## Activity Diagram
- procedural flow of control between 2+ class objects while processing activity
- higher - business unit lvl / lower lvl - internal class actions

### Notation
Same elements as statechart:
- start: solid circle
- activity: rounded rectangle
- transition lines
- decision points
- termination point

Can be grouped into swimlanes: lane = object

## Component Diagram
- physical view of the system: dependencies on other software components
- high level / package level
- dotted arrow = dependency

## Deployment Diagram
- system phisically deployed in hardware environment: where components run
- models the physical runtime

### Notation
Like component diagram, plus: 

node:
- physical / virtual machine node
- 3D cube with name on top
- naming convention: `instance name : instance type`
  (e.g., "w3reporting.myco.com : Application Server")

# The Class Diagram
Structure diagrams:
- class diagram
- component and or object diagram

Behaviour diagrams
- activity
- use cases
- sequence

## Structure Diagrams
- static structure of the system: elements irrespective of time
- types and their instances + relationships
- for: design validation and design communication

## Basics
Classifiers: class, interface, data type, component

### Class name
- compulsory
- name / attributes / methods

### Attribute List
format: 
- `name : attribute type`
- `name : attribute type = default value`

### Class Operations List
- format: `name(parameter list) : type of value returned`
- eg.: `delayFlight(numberOfMinutes : Minutes) : Date`
- in/out indicator (older languages)

### Inheritance
- solid line from child with closed unfilled arrowhead
- tree notation: when 2+ child classes - lines merge

### Abstract Classes and Operations
- name italicized

### Associations
- Bi-directional:
  solid line + role name & multiplicity value at both ends
- Uni-Directional:
  open arrowhead + role name & multiplicity at one end

### Packages
- large rectangle with tab above its upper left corner
- inside rectangle / outside + line + circled plus sign

## Beyond the Basics
### Interfaces
- like class but `«interface»` before name
- implementation: dotted line with empty arrowhead

### More Associations
#### Association Class
- includes information about the relationship
- dotted line to association

#### Aggregation
- whole to its parts relationship - car and wheels
- also composition aggregation - company and departments
- basic aggregation: unfilled diamond, open arrowhead, name & multiplicity
- composition: filled diamond, open arrowhead, name & multiplicity

#### Reflexive Associations
- class associated with itself, eg.: employee - manager

### Visibility
` ` | Visibility
--- | ----------
`+` | Public
`#` | Protected
`-` | Private
`~` | Package

## UML 2 Additions
### Instance Specification
Notation: `Instance Name : Class Name`

### Roles
- more generic
- role and class's name

### Internal Structures
How class (or sg) is composed


  
# The Sequence Diagram

[IBM Sequence Diagram](https://www.ibm.com/developerworks/rational/library/3101.html)

## Purpose
Interactions between objects in the sequential order

It can:
- describe current affairs
- act as requirements document
- documenting how a future system should behave
- transition from requirements as use cases

## The Notation
Frame:
  - boundary (incoming / outcoming messages modeled)
  - label in top-left corner

format: `sd Diagram Type Diagram Name`

## The Basics
defines event sequences that result in some desired outcome

focus on the order in which messages occur

### Lifelines
- across the top
- roles or object instances
- box (name inside) with dashed line
- format: `Instance Name : Class Name`
- underlined if a specific instance (not e.g. role)

### Messages
- line with solid arrowhead (synchronous) / stick arrowhead (asynchronous)
- message/method name on line (as recieving object has it)
- return message: dotted with open arrowhead, return value
- messages to self too

### Guards
Condition
in front of message name, e.g. `[pastDueBalance = 0]`

### Combined Fragments
groups sets of messages together to show conditional flow (UML 2)
#### Alternatives
- mutually exclusive choice between 2+ message sequences
- frame with `alt` label, divided into _operands_ by a dashed line
- operands have guards at the top
- if no guard: `[else]` guard is assumed

#### Option
- for a suquence that occurs or not, given a condition
- `opt` label in frame, guard...

#### Loops
- frame, `loop` label, guard...
- guard: boolean, or `minint = number` / `maxint = number` (num of iterations)

## Beyond the Basics
### Referencing Another Sequence Diagram
- UML2: "Interaction Occurrence": compose primitive sd to complex
- frame, `ref` label, inside: `sequence diagram name(arguments) : return value`
- the extended sd has the label:
`Diagram Type Diagram Name (Parameter Type : Parameter Name): Return Value Type`
- extended sd has input parameter and return value as a lifeline

### Gates
- the same but with arrows ending at the frame
- extended sd has entry gate (message)

### Combined Fragments
#### Break
- like opt, but breaks enclosing interaction, label: `break`, guard...
- used to model exception handling

#### Parallel
`par` label


