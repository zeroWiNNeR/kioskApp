package com.adamanta.kioskapp;

public interface IMainActivity {

    /**
     * Вызывает рендеринг UI панели в mainactivity_layout
     * @param isOnline - параметр, принимаемый от фонового треда,
     *                     мониторящего состояние подключения к серверу
     */
    void updateUI(boolean isOnline);

}
