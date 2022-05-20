const clickAll = () => {
    const buttons = document.getElementsByClassName('btn btn-default ng-binding');
    for (let i = 0; i < buttons.length; i++) {
        buttons[i].click();
    }
};
