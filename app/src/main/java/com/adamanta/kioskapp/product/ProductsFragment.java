package com.adamanta.kioskapp.product;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.product.adapters.FirstRVAdapter;
import com.adamanta.kioskapp.product.adapters.SecondRVAdapter;
import com.adamanta.kioskapp.product.fragments.productImagesFragment.ProductImagesFragment;
import com.adamanta.kioskapp.product.model.Category;
import com.adamanta.kioskapp.product.model.CategoryAndProduct;
import com.adamanta.kioskapp.product.utils.CategoriesDBHelper;
import com.adamanta.kioskapp.product.utils.ProductsDBHelper;
import com.adamanta.kioskapp.product.utils.Utils;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ProductsFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ProductsDBHelper productsDBHelper;
    private CategoriesDBHelper categoriesDBHelper;
    private CategoryAndProduct product;
    private ImageView productsFragmentMainImgV, productsColumnImgV, productMainImgV, addToFavoritesImgBtn;
    private TextView productFullNameTV, manufacturerTV, weightTV, priceAllTV, priceAllRubTV, countAllTV, sizeTypeTV, articleTV, barcodeTV, compositionTV;
    private ImageButton minusProductImgBtn, plusProductImgBtn, showCartBtn;
    private Button addProductToCartBtn;
    private FirstRVAdapter firstRVAdapter;
    private SecondRVAdapter secondRVAdapter;
    private Stack<Long> pickedCategories = new Stack<>();
    private BigDecimal currentPriceAll;

    public static ProductsFragment newInstance(int num) {
        ProductsFragment productsFragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
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

        productsFragmentMainImgV = view.findViewById(R.id.fragment_products_main_imgv);
        productsColumnImgV = view.findViewById(R.id.fragment_products_productscolumn_imgv);
        productMainImgV = view.findViewById(R.id.fragment_products_product_imgv);
        productMainImgV.setOnClickListener(this);
        addToFavoritesImgBtn = view.findViewById(R.id.fragment_products_addtofavorites_imgbtn);
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
        showCartBtn = view.findViewById(R.id.fragment_products_showcart_btn);
        articleTV = view.findViewById(R.id.fragment_products_productarticle_tv);
        barcodeTV = view.findViewById(R.id.fragment_products_productbarcode_tv);
        compositionTV = view.findViewById(R.id.fragment_products_composition_tv);

        productsDBHelper = new ProductsDBHelper(view.getContext());
        categoriesDBHelper = new CategoriesDBHelper(view.getContext());

        List<Category> categories = categoriesDBHelper.getCategoriesByParentCategory(0L);
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

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragment_products_mainmenu_imgbtn:
                if (getActivity() != null)
                    getActivity().getSupportFragmentManager().beginTransaction().remove(ProductsFragment.this).commit();
                break;

            case R.id.fragment_products_back_menu_imgbtn:
                if (pickedCategories.size() > 1) {
                    productsFragmentMainImgV.setVisibility(View.INVISIBLE);
                    productsColumnImgV.setVisibility(View.VISIBLE);
                    pickedCategories.pop();
                    clearProductCard();
                    List<CategoryAndProduct> categories = categoriesDBHelper.getListCategoryAndProductByParentCategory(pickedCategories.peek());
                    List<CategoryAndProduct> products = productsDBHelper.getListCategoryAndProductByParentCategory(pickedCategories.peek());
                    List<CategoryAndProduct> categoryAndProductList = new ArrayList<>();
                    categoryAndProductList.addAll(categories);
                    categoryAndProductList.addAll(products);
                    secondRVAdapter.updateListData(categoryAndProductList);
                } else if (pickedCategories.size() == 1) {
                    productsFragmentMainImgV.setVisibility(View.VISIBLE);
                    productsColumnImgV.setVisibility(View.INVISIBLE);
                    clearProductCard();
                    secondRVAdapter.updateListData(new ArrayList<>());
                    firstRVAdapter.resetRVData();
                    pickedCategories.pop();
                } else {
                    if (getActivity() != null)
                        getActivity().getSupportFragmentManager().beginTransaction().remove(ProductsFragment.this).commit();
                }
                break;

            case R.id.fragment_products_minusproduct_imgbtn:
                BigDecimal currentCountAll = new BigDecimal(countAllTV.getText().toString());
                currentCountAll = currentCountAll.subtract(product.getSizeStep());
                if (currentCountAll.compareTo(product.getMinSize()) >= 0) {
                    if (currentCountAll.stripTrailingZeros().scale() <= 0) {
                        countAllTV.setText(String.valueOf(currentCountAll.toBigIntegerExact()));
                    } else {
                        countAllTV.setText(String.valueOf(currentCountAll));
                    }
                    currentPriceAll = currentPriceAll.subtract(product.getPricePerSizeStep());
                    priceAllTV.setText(String.valueOf(currentPriceAll));
                    addProductToCartBtn.setText("Добавить");
                }
                break;

            case R.id.fragment_products_plusproduct_imgbtn:
                currentCountAll = new BigDecimal(countAllTV.getText().toString());
                currentCountAll = currentCountAll.add(product.getSizeStep());
                if (currentCountAll.compareTo(product.getMaxSize()) <= 0) {
                    if (currentCountAll.stripTrailingZeros().scale() <= 0) {
                        countAllTV.setText(String.valueOf(currentCountAll.toBigIntegerExact()));
                    } else {
                        countAllTV.setText(String.valueOf(currentCountAll));
                    }
                    currentPriceAll = currentPriceAll.add(product.getPricePerSizeStep());
                    priceAllTV.setText(String.valueOf(currentPriceAll));
                    addProductToCartBtn.setText("Добавить");
                }
                break;

            case R.id.fragment_products_addtocart_btn:
                addProductToCartBtn.setText(R.string.addtocart);
                break;


            case R.id.fragment_products_addtofavorites_imgbtn:
                break;

            case R.id.fragment_products_product_imgv:
                Fragment productImagesFragment = ProductImagesFragment.newInstance(product.getArticle(), product.getImagesInfo());
                if (getActivity() != null) {
                    FragmentTransaction ftr = getActivity().getSupportFragmentManager().beginTransaction();
                    ftr.add(R.id.mainactivity_fragment_layout, productImagesFragment, "ProductImagesFragment");
                    ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ftr.addToBackStack(null);
                    ftr.commit();
                }
                break;

            default: break;
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
        secondRVAdapter.updateListData(categoryAndProductList);

        pickedCategories.push(parentCategory);
    }

    public void resetPickedCategories() {
        pickedCategories.clear();
        productsFragmentMainImgV.setVisibility(View.VISIBLE);
        productsColumnImgV.setVisibility(View.INVISIBLE);
        secondRVAdapter.updateListData(new ArrayList<>());
        clearProductCard();
    }

    public void setProductCard(CategoryAndProduct product) {
        this.product = product;

        TextView priceAllRubProductTV = view.findViewById(R.id.fragment_products_productpriceallrub_tv);
        priceAllRubProductTV.setVisibility(View.VISIBLE);

        minusProductImgBtn.setVisibility(View.VISIBLE);
        minusProductImgBtn.setClickable(true);
        minusProductImgBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8BC34A")));

        plusProductImgBtn.setVisibility(View.VISIBLE);
        plusProductImgBtn.setClickable(true);
        plusProductImgBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8BC34A")));

        priceAllTV.setVisibility(View.VISIBLE);
        priceAllRubTV.setVisibility(View.VISIBLE);
        countAllTV.setVisibility(View.VISIBLE);

        addProductToCartBtn.setVisibility(View.VISIBLE);
        addProductToCartBtn.setClickable(true);
        addProductToCartBtn.setText("Добавить");
        addProductToCartBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8BC34A")));

        showCartBtn.setVisibility(View.VISIBLE);

        String[] imagesInfo = product.getImagesInfo().split("\\|");
        String[] images = new String[0];
        if (imagesInfo.length == 1) {
            images = imagesInfo[0].split(";");
        } else if (imagesInfo.length > 1) {
            images = imagesInfo[1].split(";");
        }
        String mainProductImgName = "1.webp";
        for (String image : images) {
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
        productMainImgV.setVisibility(View.VISIBLE);

        productFullNameTV.setText(product.getFullName());
        manufacturerTV.setText(product.getManufacturer());
        weightTV.setText(product.getWeight());
        DecimalFormat countFormat = new DecimalFormat("###.##");
        countAllTV.setText(countFormat.format(product.getMinSize()));
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

        if (Utils.checkInFavorites(product.getName())) {
            addToFavoritesImgBtn.setImageResource(R.drawable.ic_favorites);
        } else {
            addToFavoritesImgBtn.setImageResource(R.drawable.ic_notfavorites);
        }
        addToFavoritesImgBtn.setVisibility(View.VISIBLE);
    }

    private void clearProductCard() {
        minusProductImgBtn.setVisibility(View.INVISIBLE);
        plusProductImgBtn.setVisibility(View.INVISIBLE);

        productFullNameTV.setText(null);
        manufacturerTV.setText(null);
        weightTV.setText(null);
        priceAllTV.setText(null);
        priceAllRubTV.setVisibility(View.INVISIBLE);
        countAllTV.setText(null);
        sizeTypeTV.setText(null);
        articleTV.setText(null);
        barcodeTV.setText(null);
        compositionTV.setText(null);

        addProductToCartBtn.setVisibility(View.INVISIBLE);
        showCartBtn.setVisibility(View.INVISIBLE);

        productMainImgV.setImageBitmap(null);
        productMainImgV.setVisibility(View.INVISIBLE);

        addToFavoritesImgBtn.setVisibility(View.INVISIBLE);

        productFullNameTV.setText(null);
        manufacturerTV.setText(null);

        weightTV.setText(null);
        priceAllTV.setText(null);

        articleTV.setText(null);
        barcodeTV.setText(null);

        productsColumnImgV.setVisibility(View.INVISIBLE);
    }

}
