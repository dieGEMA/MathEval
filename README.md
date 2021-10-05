# MathEval

WIP.
A lexer and parser for mathematical expressions.
Currently able to read text from the terminal and divide it into tokens.
These tokens can then be turned into a tree, representing the structure and operator priorities in the entered mathematical expressions.
Works with numbers, simple mathematical operators (+, -, *, / etc) and trigonometric functions (sine, cosine, tangent etc).

#### Current WIPs:
- Invert operators and signs of numbers and symbols if they are in parentheses with a minus sign before it, e.g. `-(3+3) -> (-3-3)` 
- Evaluate expressions without symbols

#### Planned for the future:
- Evaluate expressions with symbols
- Build a GUI
- Draw functions in the GUI
