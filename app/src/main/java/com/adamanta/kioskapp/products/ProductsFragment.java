package com.adamanta.kioskapp.products;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.IMainActivity;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.favorites.utils.FavoritesDBHelper;
import com.adamanta.kioskapp.products.adapters.FirstRVAdapter;
import com.adamanta.kioskapp.products.adapters.SecondRVAdapter;
import com.adamanta.kioskapp.products.fragments.productImagesFragment.ProductImagesFragment;
import com.adamanta.kioskapp.products.model.Category;
import com.adamanta.kioskapp.products.model.CategoryAndProduct;
import com.adamanta.kioskapp.products.model.Product;
import com.adamanta.kioskapp.products.utils.CategoriesDBHelper;
import com.adamanta.kioskapp.products.utils.ProductsDBHelper;
import com.adamanta.kioskapp.products.utils.Utils;
import com.adamanta.kioskapp.shopcart.ShopCartFragment;
import com.adamanta.kioskapp.shopcart.model.ShopCartProduct;
import com.adamanta.kioskapp.shopcart.utils.ShopCartDBHelper;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductsFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ProductsDBHelper productsDBHelper;
    private CategoriesDBHelper categoriesDBHelper;
    private FavoritesDBHelper favoritesDBHelper;
    private CategoryAndProduct product;
    private ConstraintLayout productCardCL;
    private ImageView productsFragmentMainImgV, productsColumnImgV, productMainImgV, addToFavoritesImgBtn;
    private TextView productFullNameTV, manufacturerTV, weightTV, priceAllTV, priceAllRubTV, countAllTV, sizeTypeTV, articleTV, barcodeTV, compositionTV;
    private ImageButton minusProductImgBtn, plusProductImgBtn;
    private Button addProductToCartBtn;
    private FirstRVAdapter firstRVAdapter;
    private SecondRVAdapter secondRVAdapter;
    private BigDecimal currentPriceAll;
    private BigDecimal currentAllCount;

    public static ProductsFragment newInstance(long article, long parentCategory) {
        ProductsFragment productsFragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putLong("article", article);
        args.putLong("parentCategory", parentCategory);
        productsFragment.setArguments(args);
        return productsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_products, container, false);

        ImageButton closeFragmentBtn = view.findViewById(R.id.fragment_products_mainmenu_imgbtn);
        closeFragmentBtn.setOnClickListener(this);
        ImageButton backFragmentBtn = view.findViewById(R.id.fragment_products_back_menu_imgbtn);
        backFragmentBtn.setOnClickListener(this);

        productCardCL = view.findViewById(R.id.fragment_products_productcard_constraintlayout);
        productsFragmentMainImgV = view.findViewById(R.id.fragment_products_main_imgv);
        productsColumnImgV = view.findViewById(R.id.fragment_products_productscolumn_imgv);
        productMainImgV = view.findViewById(R.id.fragment_products_product_imgv);
        productMainImgV.setOnClickListener(this);
        addToFavoritesImgBtn = view.findViewById(R.id.fragment_products_addtofavorites_imgbtn);
        addToFavoritesImgBtn.setOnClickListener(this);
        productFullNameTV = view.findViewById(R.id.fragment_products_productfullname_tv);
        manufacturerTV = view.findViewById(R.id.fragment_products_productmanufacturer_tv);
        weightTV = view.findViewById(R.id.fragment_products_productweight_tv);
        priceAllTV = view.findViewById(R.id.fragment_products_productpriceall_tv);
        priceAllRubTV = view.findViewById(R.id.fragment_products_productpriceallrub_tv);
        minusProductImgBtn = view.findViewById(R.id.fragment_products_minusproduct_imgbtn);
        minusProductImgBtn.setOnClickListener(this);
        countAllTV = view.findViewById(R.id.fragment_products_productcountall_tv);
        sizeTypeTV = view.findViewById(R.id.fragment_products_productsizetype_tv);
        plusProductImgBtn = view.findViewById(R.id.fragment_products_plusproduct_imgbtn);
        plusProductImgBtn.setOnClickListener(this);
        addProductToCartBtn = view.findViewById(R.id.fragment_products_addtocart_btn);
        addProductToCartBtn.setOnClickListener(this);
        ImageButton showShopCartBtn = view.findViewById(R.id.fragment_products_showshopcart_btn);
        showShopCartBtn.setOnClickListener(this);
        articleTV = view.findViewById(R.id.fragment_products_productarticle_tv);
        barcodeTV = view.findViewById(R.id.fragment_products_productbarcode_tv);
        compositionTV = view.findViewById(R.id.fragment_products_composition_tv);

        productsDBHelper = new ProductsDBHelper(view.getContext());
        categoriesDBHelper = new CategoriesDBHelper(view.getContext());
        favoritesDBHelper = new FavoritesDBHelper(view.getContext());

        List<Category> categories = categoriesDBHelper.getCategoriesByParentCategory(0L);
        Collections.sort(categories, Category::compareToByPosition);
        firstRVAdapter = new FirstRVAdapter(categories);
        final RecyclerView firstRecyclerView = view.findViewById(R.id.fragment_products_first_rv);
        firstRecyclerView.setHasFixedSize(false);
        firstRecyclerView.setAdapter(firstRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        firstRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        firstRecyclerView.setItemAnimator(itemAnimator);

        secondRVAdapter = new SecondRVAdapter(new ArrayList<>());
        final RecyclerView secondRecyclerView = view.findViewById(R.id.fragment_products_second_rv);
        secondRecyclerView.setHasFixedSize(false);
        secondRecyclerView.setAdapter(secondRVAdapter);
        final LinearLayoutManager layoutManager2 = new LinearLayoutManager(view.getContext());
        secondRecyclerView.setLayoutManager(layoutManager2);
        final RecyclerView.ItemAnimator itemAnimator2 = new DefaultItemAnimator();
        secondRecyclerView.setItemAnimator(itemAnimator2);

        if (getArguments() != null) {
            if (getArguments().getLong("parentCategory") != 0L) {
                Product product = productsDBHelper.getByArticle(getArguments().getLong("article"));
                openProductFromSearch(product);
            }
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_products_mainmenu_imgbtn) {
            if (getActivity() != null)
                getActivity().getSupportFragmentManager().beginTransaction().remove(ProductsFragment.this).commit();

        } else if (v.getId() == R.id.fragment_products_back_menu_imgbtn) {
            Long parentCategoryLong = secondRVAdapter.getParentCategoryFirstItemInList();
            if (parentCategoryLong == null) {
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().beginTransaction().remove(ProductsFragment.this).commit();
            } else {
                CategoryAndProduct parentCategory = categoriesDBHelper.getFirstCategoryByCategory(parentCategoryLong);
                if (parentCategory.getParentCategory() == 0L) {
                    productsFragmentMainImgV.setVisibility(View.VISIBLE);
                    productsColumnImgV.setVisibility(View.INVISIBLE);
                    productCardCL.setVisibility(View.INVISIBLE);
                    resetPickedCategories();
                    firstRVAdapter.resetRVData();
                    secondRVAdapter.updateListData(new ArrayList<>());
                } else {
                    productsFragmentMainImgV.setVisibility(View.INVISIBLE);
                    productsColumnImgV.setVisibility(View.VISIBLE);
                    productCardCL.setVisibility(View.INVISIBLE);
                    List<CategoryAndProduct> categories = categoriesDBHelper.getListCategoryAndProductByParentCategory(parentCategory.getParentCategory());
                    List<CategoryAndProduct> products = productsDBHelper.getListCategoryAndProductByParentCategory(parentCategory.getParentCategory());
                    List<CategoryAndProduct> categoryAndProductList = new ArrayList<>();
                    categoryAndProductList.addAll(categories);
                    categoryAndProductList.addAll(products);
                    Collections.sort(categoryAndProductList, CategoryAndProduct::compareToByPosition);
                    secondRVAdapter.updateListData(categoryAndProductList);
                }
            }

        } else if (v.getId() == R.id.fragment_products_product_imgv) {
            if (getActivity() != null) {
                Fragment productImagesFragment = ProductImagesFragment.newInstance(product.getArticle(), product.getImagesNamesAndPositions());
                FragmentTransaction ftr = getActivity().getSupportFragmentManager().beginTransaction();
                ftr.add(R.id.activity_main_fragment_layout, productImagesFragment, "ProductImagesFragment");
                ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftr.addToBackStack(null);
                ftr.commit();
            }

        } else if (v.getId() == R.id.fragment_products_addtofavorites_imgbtn) {
            if (favoritesDBHelper.findByArticle(product.getArticle())) {
                favoritesDBHelper.deleteByArticle(product.getArticle());
                addToFavoritesImgBtn.setImageResource(R.drawable.ic_notfavorites);
            } else if (!favoritesDBHelper.findByArticle(product.getArticle())) {
                favoritesDBHelper.saveProduct(product.getArticle());
                addToFavoritesImgBtn.setImageResource(R.drawable.ic_favorites);
            }

        } else if (v.getId() == R.id.fragment_products_minusproduct_imgbtn) {
            BigDecimal currentCountAll = currentAllCount;
            currentCountAll = currentCountAll.subtract(product.getSizeStep());
            if (currentCountAll.compareTo(product.getMinSize()) >= 0) {
                currentAllCount = currentAllCount.subtract(product.getSizeStep());
                if (currentCountAll.stripTrailingZeros().scale() <= 0) {
                    countAllTV.setText(String.valueOf(currentCountAll.toBigIntegerExact()));
                } else {
                    countAllTV.setText(String.valueOf(currentCountAll));
                }
                currentPriceAll = currentPriceAll.subtract(product.getPricePerSizeStep());
                priceAllTV.setText(String.valueOf(currentPriceAll));
                addProductToCartBtn.setText("Добавить");
            }

        } else if (v.getId() == R.id.fragment_products_plusproduct_imgbtn) {
            BigDecimal currentCountAll = currentAllCount;
            currentCountAll = currentCountAll.add(product.getSizeStep());
            if (currentCountAll.compareTo(product.getMaxSize()) <= 0) {
                currentAllCount = currentAllCount.add(product.getSizeStep());
                if (currentCountAll.stripTrailingZeros().scale() <= 0) {
                    countAllTV.setText(String.valueOf(currentCountAll.toBigIntegerExact()));
                } else {
                    countAllTV.setText(String.valueOf(currentCountAll));
                }
                currentPriceAll = currentPriceAll.add(product.getPricePerSizeStep());
                priceAllTV.setText(String.valueOf(currentPriceAll));
                addProductToCartBtn.setText("Добавить");
            }

        } else if (v.getId() == R.id.fragment_products_addtocart_btn) {
            ShopCartDBHelper shopCartDBHelper = new ShopCartDBHelper(view.getContext());
            ShopCartProduct shopCartProduct = shopCartDBHelper.getByArticle(product.getArticle());
            if (shopCartProduct == null) {
                if (currentAllCount.compareTo(product.getMaxSize()) <= 0) {
                    shopCartDBHelper.addNewShopCartProduct(product, currentAllCount);
                    addProductToCartBtn.setText(R.string.addtocart);
                } else {
                    ((IMainActivity) view.getContext()).showToastMessage("Превышено допустимое кол-во товара для покупки!");
                }
            } else {
                BigDecimal summaryAllCount = currentAllCount.add(shopCartProduct.getAllCount());
                if (summaryAllCount.compareTo(product.getMaxSize()) <= 0) {
                    shopCartDBHelper.addAdditionalCountToShopCartProduct(shopCartProduct, currentAllCount);
                    addProductToCartBtn.setText(R.string.addtocart);
                } else {
                    String message;
                    if (product.getSizeStep().stripTrailingZeros().scale() <= 0) {
                        message = "Не удалось добавить данное количество товара, не хватает на складе! " +
                                "\nМожно добавить: " + product.getMaxSize().subtract(shopCartProduct.getAllCount()).toBigIntegerExact();
                    } else {
                        message = "Не удалось добавить данное количество товара, не хватает на складе! " +
                                "\nМожно добавить: " + product.getMaxSize().subtract(shopCartProduct.getAllCount());
                    }
                    ((IMainActivity) view.getContext()).showToastMessage(message);
                }
            }

        } else if (v.getId() == R.id.fragment_products_showshopcart_btn) {
            if (getActivity() != null) {
                Fragment shopCartFragment = ShopCartFragment.newInstance(123);
                FragmentTransaction ftr = getActivity().getSupportFragmentManager().beginTransaction();
                ftr.add(R.id.activity_main_fragment_layout, shopCartFragment, "ShopCartFragment");
                ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftr.addToBackStack(null);
                ftr.commit();
            }
        }
    }

    public void updateSecondRVData(long parentCategory) {
        productsFragmentMainImgV.setVisibility(View.INVISIBLE);
        productsColumnImgV.setVisibility(View.VISIBLE);

        List<CategoryAndProduct> categories = categoriesDBHelper.getListCategoryAndProductByParentCategory(parentCategory);
        List<CategoryAndProduct> products = productsDBHelper.getListCategoryAndProductByParentCategory(parentCategory);
        List<CategoryAndProduct> categoryAndProductList = new ArrayList<>();
        categoryAndProductList.addAll(categories);
        categoryAndProductList.addAll(products);
        Collections.sort(categoryAndProductList, CategoryAndProduct::compareToByPosition);
        secondRVAdapter.updateListData(categoryAndProductList);

    }

    public void resetPickedCategories() {
        productsFragmentMainImgV.setVisibility(View.VISIBLE);
        productsColumnImgV.setVisibility(View.INVISIBLE);
        productCardCL.setVisibility(View.INVISIBLE);
        secondRVAdapter.updateListData(new ArrayList<>());
    }

    public void setProductCard(CategoryAndProduct product) {
        this.product = product;

        productCardCL.setVisibility(View.VISIBLE);
        minusProductImgBtn.setClickable(true);
        plusProductImgBtn.setClickable(true);
        addProductToCartBtn.setClickable(true);

        String mainProductImgName = "1.webp";
        for (String image : product.getImagesNamesAndPositions().split(";")) {
            String position = image.split("=")[1];
            if (position.equals("1")) {
                mainProductImgName = image.split("=")[0];
            }
        }

        File filesDir = view.getContext().getFilesDir();
        File productImagesDir = new File(filesDir.getAbsolutePath() + "/images/products/" + product.getArticle() + "/");
        File productMainImg = new File(productImagesDir, mainProductImgName);
        Bitmap bitmap = Utils.getScaledBitmap(productMainImg.getAbsolutePath(), 250, 350);
        productMainImgV.setImageBitmap(bitmap);

        productFullNameTV.setText(product.getFullName());
        manufacturerTV.setText(product.getManufacturer());
        weightTV.setText(product.getWeight());
        DecimalFormat countFormat = new DecimalFormat("###.##");
        countAllTV.setText(countFormat.format(product.getMinSize()));
        currentAllCount = product.getMinSize();
        sizeTypeTV.setText(product.getSizeType());
        BigDecimal priceAll = product.getMinSize().divide(product.getSizeStep()).multiply(product.getPricePerSizeStep());
        DecimalFormat priceFormat = new DecimalFormat("###.##");
        priceAllTV.setText(priceFormat.format(priceAll));
        currentPriceAll = priceAll;

        if (product.getStockQuantity().compareTo(new BigDecimal(0)) == 0) {
            minusProductImgBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
            minusProductImgBtn.setClickable(false);
            plusProductImgBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
            plusProductImgBtn.setClickable(false);
            addProductToCartBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
            addProductToCartBtn.setClickable(false);
            priceAllTV.setText("Нет в наличии");
            countAllTV.setText("0");
        }

        String article = product.getArticle().toString();
        switch (article.length()) {
            case 1:
                article = "Арт. 00000" + article;
                break;
            case 2:
                article = "Арт. 0000" + article;
                break;
            case 3:
                article = "Арт. 000" + article;
                break;
            case 4:
                article = "Арт. 00" + article;
                break;
            case 5:
                article = "Арт. 0" + article;
                break;
            default:
                article = "Арт. " + article;
                break;
        }
        articleTV.setText(article);
        barcodeTV.setText(String.valueOf("Ш/н " + product.getBarcode()));
        compositionTV.setText(product.getComposition());

        if (favoritesDBHelper.findByArticle(product.getArticle())) {
            addToFavoritesImgBtn.setImageResource(R.drawable.ic_favorites);
        } else {
            addToFavoritesImgBtn.setImageResource(R.drawable.ic_notfavorites);
        }
    }

    public void openProductFromSearch(Product product) {
        productsFragmentMainImgV.setVisibility(View.INVISIBLE);
        productsColumnImgV.setVisibility(View.VISIBLE);

        List<CategoryAndProduct> categories = categoriesDBHelper.getListCategoryAndProductByParentCategory(product.getParentCategory());
        List<CategoryAndProduct> products = productsDBHelper.getListCategoryAndProductByParentCategory(product.getParentCategory());
        List<CategoryAndProduct> categoryAndProductList = new ArrayList<>();
        categoryAndProductList.addAll(categories);
        categoryAndProductList.addAll(products);
        Collections.sort(categoryAndProductList, CategoryAndProduct::compareToByPosition);
        secondRVAdapter.updateListData(categoryAndProductList);

        CategoryAndProduct openingProduct = new CategoryAndProduct();
        openingProduct.setId(product.getId());
        openingProduct.setType(product.getType());
        openingProduct.setParentCategory(product.getParentCategory());
        openingProduct.setPosition(product.getPosition());
        openingProduct.setIsEnable(product.getIsEnable());
        openingProduct.setName(product.getName());
        openingProduct.setFullName(product.getFullName());
        openingProduct.setArticle(product.getArticle());
        openingProduct.setBarcode(product.getBarcode());
        openingProduct.setWeight(product.getWeight());
        openingProduct.setMinSize(product.getMinSize());
        openingProduct.setSizeStep(product.getSizeStep());
        openingProduct.setPricePerSizeStep(product.getPricePerSizeStep());
        openingProduct.setWeightPerSizeStep(product.getWeightPerSizeStep());
        openingProduct.setMaxSize(product.getMaxSize());
        openingProduct.setSizeType(product.getSizeType());
        openingProduct.setStockQuantity(product.getStockQuantity());
        openingProduct.setManufacturer(product.getManufacturer());
        openingProduct.setDescription(product.getDescription());
        openingProduct.setComposition(product.getComposition());
        openingProduct.setPrevComposition(product.getPrevComposition());
        openingProduct.setPrevCompositionDate(product.getPrevCompositionDate());
        openingProduct.setPrevPrevComposition(product.getPrevPrevComposition());
        openingProduct.setPrevPrevCompositionDate(product.getPrevPrevCompositionDate());
        openingProduct.setInformation(product.getInformation());
        openingProduct.setImagesNamesAndPositions(product.getImagesNamesAndPositions());
        openingProduct.setImagesInfo(product.getImagesInfo());
        setProductCard(openingProduct);
    }

}
