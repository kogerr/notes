const selectByInput = (input, onEquals, onOperation, onNumber) => {
    if (/=/.test(input)) {
        return onEquals;
    } else if (/[+\-*\/]/.test(input)) {
        return onOperation;
    } else if (/[\d.]/.test(input)) {
        return onNumber;
    } else if('clear' === input) {
        return waitForNew;
    }
};

const waitForNew = input => selectByInput(input, waitForNew, waitForNew, registerNumber(displayLower(input)));

const concat = (soFar, input) => input === '.' && soFar.includes('.') ? soFar : soFar + input;

const registerNumber = input => nextInput => selectByInput(nextInput, registerNumber(input), initOperation(input, selectOperation(nextInput)), registerNumber(concat(input, nextInput)));

const addition = firstOperand => secondOperand => parseFloat(firstOperand) + parseFloat(secondOperand);

const subtraction = firstOperand => secondOperand => parseFloat(firstOperand) - parseFloat(secondOperand);

const multiplication = firstOperand => secondOperand => parseFloat(firstOperand) * parseFloat(secondOperand);

const division = firstOperand => secondOperand =>
    secondOperand === '0' ? 0 : parseFloat(firstOperand) / parseFloat(secondOperand);

const selectOperation = operator => {
    switch (operator) {
        case '+':
            return addition;
        case '-':
            return subtraction;
        case '*':
            return multiplication;
        case '/':
            return division;
    }
};

const initOperation = (firstOperand, operation) => input => selectByInput(input, evaluate(operation(firstOperand), firstOperand), initOperation(firstOperand, selectOperation(input)), registerSecondOperand(operation(firstOperand), input));

const evaluateAndNewOperation = (ongoingOperation, secondOperand, input) => initOperation(ongoingOperation(secondOperand), selectOperation(input));

const registerSecondOperand = (ongoingOperation, secondOperand) => input => selectByInput(input, evaluate(ongoingOperation, secondOperand), evaluateAndNewOperation(ongoingOperation, secondOperand, input), registerSecondOperand(ongoingOperation, concat(secondOperand, input)));

const evaluate = (ongoingOperation, secondOperand) => {displayLower(ongoingOperation(secondOperand)); return waitForNew;};

// const setInnerText = (elementId, text) => {
//     document.getElementById(elementId).innerText = text;
// };
//
// const clearInnerHTML = (elementId) => {
//     document.getElementById(elementId).innerHTML = '&nbsp;';
// };

const displayUpper = text => {
    document.getElementById('upper-line').innerText = text;
    return text;
};

const displayLower = text => {
    document.getElementById('lower-line').innerText = text;
    return text;
};
const displayLower2 = (f, ...arg) => arg2 => {document.getElementById('lower-line').innerText = arg; return f(...arg)(arg2);};

const inputFeeder = function(initialFunction){
    let currentFunction = initialFunction;

    return event => {
        console.log(event.target.dataset.identifier);
        currentFunction = currentFunction(event.target.dataset.identifier);
    }
}(waitForNew);

document.getElementById('numpad').addEventListener('click', inputFeeder);
