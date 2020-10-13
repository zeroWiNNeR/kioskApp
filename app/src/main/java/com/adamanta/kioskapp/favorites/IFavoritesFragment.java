package com.adamanta.kioskapp.favorites;

import com.adamanta.kioskapp.product.model.Product;

public interface IFavoritesFragment {

    /**
     * Вызывает метод открытого фрагмента FavoritesFragment,
     * который отвечает за отображение данных в ProductCard
     * @param product - данные об отображаемом товаре
     */
    void favoritesFragmentSetProductCard(Product product);

}
