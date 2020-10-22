package com.adamanta.kioskapp.favorites;

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

import com.adamanta.kioskapp.IMainActivity;
import com.adamanta.kioskapp.R;
import com.adamanta.kioskapp.favorites.utils.FavoritesDBHelper;
import com.adamanta.kioskapp.products.fragments.productImagesFragment.ProductImagesFragment;
import com.adamanta.kioskapp.products.model.CategoryAndProduct;
import com.adamanta.kioskapp.products.model.Product;
import com.adamanta.kioskapp.products.utils.ProductsDBHelper;
import com.adamanta.kioskapp.products.utils.Utils;
import com.adamanta.kioskapp.shopcart.ShopCartFragment;
import com.adamanta.kioskapp.shopcart.model.ShopCartProduct;
import com.adamanta.kioskapp.shopcart.utils.ShopCartDBHelper;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ImageView productMainImgV;
    private TextView productFullNameTV, manufacturerTV, weightTV, priceAllTV, priceAllRubTV, countAllTV, sizeTypeTV, articleTV, barcodeTV, compositionTV;
    private ImageButton minusProductImgBtn, plusProductImgBtn, showCartBtn;
    private Button addProductToCartBtn;
    private Product product;
    private BigDecimal currentPriceAll;
    private BigDecimal currentAllCount;

    public static FavoritesFragment newInstance(int num) {
        FavoritesFragment f = new FavoritesFragment();
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, container, false);

        FavoritesDBHelper favoritesDBHelper = new FavoritesDBHelper(view.getContext());
        ProductsDBHelper productsDBHelper = new ProductsDBHelper(view.getContext());
        long[] favoriteProductArticles = favoritesDBHelper.getAllValues();
        List<Product> favoriteProductsList = new ArrayList<>();
        for (long article : favoriteProductArticles) {
            Product productFromDb = productsDBHelper.getByArticle(article);
            if (productFromDb != null)
                favoriteProductsList.add(productFromDb);
        }

        ImageButton closeFragmentBtn = view.findViewById(R.id.fragment_favorites_closefragment_imgb);
        closeFragmentBtn.setOnClickListener(this);
        productMainImgV = view.findViewById(R.id.fragment_favorites_product_imgv);
        productMainImgV.setOnClickListener(this);
        productFullNameTV = view.findViewById(R.id.fragment_favorites_productfullname_tv);
        manufacturerTV = view.findViewById(R.id.fragment_favorites_productmanufacturer_tv);
        weightTV = view.findViewById(R.id.fragment_favorites_productweight_tv);
        priceAllTV = view.findViewById(R.id.fragment_favorites_productpriceall_tv);
        priceAllRubTV = view.findViewById(R.id.fragment_favorites_productpriceallrub_tv);
        minusProductImgBtn = view.findViewById(R.id.fragment_favorites_minusproduct_imgbtn);
        minusProductImgBtn.setOnClickListener(this);
        countAllTV = view.findViewById(R.id.fragment_favorites_productcountall_tv);
        sizeTypeTV = view.findViewById(R.id.fragment_favorites_productsizetype_tv);
        plusProductImgBtn = view.findViewById(R.id.fragment_favorites_plusproduct_imgbtn);
        plusProductImgBtn.setOnClickListener(this);
        addProductToCartBtn = view.findViewById(R.id.fragment_favorites_addtocart_btn);
        addProductToCartBtn.setOnClickListener(this);
        showCartBtn = view.findViewById(R.id.fragment_favorites_showcart_btn);
        showCartBtn.setOnClickListener(this);
        articleTV = view.findViewById(R.id.fragment_favorites_productarticle_tv);
        barcodeTV = view.findViewById(R.id.fragment_favorites_productbarcode_tv);
        compositionTV = view.findViewById(R.id.fragment_favorites_composition_tv);

        FavoritesRVAdapter favoritesRVAdapter = new FavoritesRVAdapter(favoriteProductsList);
        final RecyclerView favoritesRecyclerView = view.findViewById(R.id.fragment_favorites_rv);
        favoritesRecyclerView.setHasFixedSize(false);
        favoritesRecyclerView.setAdapter(favoritesRVAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        favoritesRecyclerView.setLayoutManager(layoutManager);
        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        favoritesRecyclerView.setItemAnimator(itemAnimator);

        return view;
    }

    public void onClick(@NonNull View v) {
        if (v.getId() == R.id.fragment_favorites_closefragment_imgb) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        } else if (v.getId() == R.id.fragment_favorites_minusproduct_imgbtn) {
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
        } else if (v.getId() == R.id.fragment_favorites_plusproduct_imgbtn) {
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
        } else if (v.getId() == R.id.fragment_favorites_addtocart_btn) {
            ShopCartDBHelper shopCartDBHelper = new ShopCartDBHelper(view.getContext());
            ShopCartProduct shopCartProduct = shopCartDBHelper.getByArticle(product.getArticle());
            if (shopCartProduct == null) {
                if (currentAllCount.compareTo(product.getMaxSize()) <= 0) {
                    CategoryAndProduct categoryAndProduct = new CategoryAndProduct();
                    categoryAndProduct.setId(product.getId());
                    categoryAndProduct.setType(product.getType());
                    categoryAndProduct.setParentCategory(product.getParentCategory());
                    categoryAndProduct.setPosition(product.getPosition());
                    categoryAndProduct.setIsEnable(product.getIsEnable());
                    categoryAndProduct.setName(product.getName());
                    categoryAndProduct.setFullName(product.getFullName());
                    categoryAndProduct.setArticle(product.getArticle());
                    categoryAndProduct.setBarcode(product.getBarcode());
                    categoryAndProduct.setWeight(product.getWeight());
                    categoryAndProduct.setMinSize(product.getMinSize());
                    categoryAndProduct.setSizeStep(product.getSizeStep());
                    categoryAndProduct.setPricePerSizeStep(product.getPricePerSizeStep());
                    categoryAndProduct.setWeightPerSizeStep(product.getWeightPerSizeStep());
                    categoryAndProduct.setMaxSize(product.getMaxSize());
                    categoryAndProduct.setSizeType(product.getSizeType());
                    shopCartDBHelper.addNewShopCartProduct(categoryAndProduct, currentAllCount);
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
        } else if (v.getId() == R.id.fragment_favorites_showcart_btn) {
            if (getActivity() != null) {
                Fragment shopCartFragment = ShopCartFragment.newInstance(123);
                FragmentTransaction ftr = getActivity().getSupportFragmentManager().beginTransaction();
                ftr.add(R.id.activity_main_fragment_layout, shopCartFragment, "ShopCartFragment");
                ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftr.addToBackStack(null);
                ftr.commit();
            }
        } else if (v.getId() == R.id.fragment_favorites_product_imgv) {
            if (getActivity() != null) {
                Fragment productImagesFragment = ProductImagesFragment.newInstance(product.getArticle(), product.getImagesNamesAndPositions());
                FragmentTransaction ftr = getActivity().getSupportFragmentManager().beginTransaction();
                ftr.add(R.id.activity_main_fragment_layout, productImagesFragment, "ProductImagesFragment");
                ftr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftr.addToBackStack(null);
                ftr.commit();
            }
        }
    }

    
    public void setProductCard(Product product) {
        this.product = product;

        TextView priceAllRubProductTV = view.findViewById(R.id.fragment_favorites_productpriceallrub_tv);
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
        productMainImgV.setVisibility(View.VISIBLE);

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
    }

    public void ftr(String path, String productName) {
        ProductImagesFragment fragment = (ProductImagesFragment)getChildFragmentManager()
                .findFragmentByTag("productImagesFragment");
        if (fragment != null) {
            fragment.changeMainImage(path);
        }
    }

    public void closeProductsImagesFragment() {
        ProductImagesFragment fragment = (ProductImagesFragment)getChildFragmentManager()
                .findFragmentByTag("productImagesFragment");
        if (fragment != null) {
            FragmentTransaction ftr = getChildFragmentManager().beginTransaction().remove(fragment);
            ftr.commit();
        }
    }

}
