package com.adamanta.kioskapp;

import com.adamanta.kioskapp.products.model.CategoryAndProduct;
import com.adamanta.kioskapp.products.model.Product;

public interface IMainActivity {

  /**
   * Вызывает рендеринг UI панели в mainactivity_layout
   *
   * @param isOnline - параметр, принимаемый от фонового треда,
   *                 мониторящего состояние подключения к серверу
   */
  void updateUI(boolean isOnline);

  /**
   * Вызывает метод отбражения Toast сообщения
   * @return Toast.makeText
   */
  void showToastMessage(String message);

  /**
   * Вызывает метод открытого фрагмента ProductsFragment,
   * который отвечает за обновление данных SecondRV
   *
   * @param parentCategory - значение, по которому продукты и категории будут найдены в БД
   *                       и загружены в SecondRV
   */
  void productsFragmentUpdateSecondRVData(long parentCategory);

  /**
   * Вызывает метод открытого фрагмента ProductsFragment,
   * который обнуляет список pickedCategories
   */
  void productsFragmentResetPickedCategories();

  /**
   * Вызывает метод открытого фрагмента ProductsFragment,
   * который отвечает за открытие карточки товара
   *
   * @param product - объект открываемого товара
   */
  void productsFragmentSetProductCard(CategoryAndProduct product);

  /**
   * Вызывает метод открытия фрагмента на продукте из поиска
   */
  void productsFragmentOpenProductFromSearch(Product product);


  /**
   * Вызывает показ выбранного изображения в ProductsFragment
   *
   * @param imageAbsolutePath - Path к файлу с открываемым изображением
   */
  void productImagesFragmentChangeMainImage(String imageAbsolutePath);


  /**
   * Вызывает метод пересчета цены во фрагменте Корзина
   */
  void shopCartFragmentCalculationTotalPrice();

}
