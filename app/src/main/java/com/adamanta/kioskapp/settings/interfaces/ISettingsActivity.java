package com.adamanta.kioskapp.settings.interfaces;

public interface ISettingsActivity {

    /**
     * Вызывает метод проверки регистрации планшета
     * @return меняет стили TV и ET взависимости от присутсвтия полей регистрации в БД
     */
    void checkRegistration();

    /**
     * Вызывает метод отбражения Toast сообщения
     * @return Toast.makeText
     */
    void showToastMessage(String message);

}
