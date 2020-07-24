package com.adamanta.kioskapp.products.interfaces;

public interface UIController {

    /**
     * Вызывает рендеринг UI панели в mainactivity_layout
     * @param onlineStatus - параметр, принимаемый от фонового треда,
     *                     мониторящего состояние подключения к серверу
     */
    void updateUI(boolean onlineStatus);

}
