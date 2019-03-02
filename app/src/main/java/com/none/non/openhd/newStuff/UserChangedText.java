package com.none.non.openhd.newStuff;


//This one is called each time the user modifies some settings value
//However, there is an issue I couldn't solve yet:
//The Text Watcher is called as soon as anything was typed, not as soon as the user finished typing.
//Therefore I use the extra 'Apply' button
public interface UserChangedText {
    void onTextChanged(final String newText);
}
