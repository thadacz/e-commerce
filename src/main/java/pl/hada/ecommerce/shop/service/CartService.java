package pl.hada.ecommerce.shop.service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import pl.hada.ecommerce.exeption.ResourceNotFoundException;
import pl.hada.ecommerce.shop.model.Cart;
import pl.hada.ecommerce.shop.model.CartItem;
import pl.hada.ecommerce.shop.model.Product;
import pl.hada.ecommerce.shop.repository.CartItemRepository;
import pl.hada.ecommerce.shop.repository.CartRepository;
import pl.hada.ecommerce.shop.repository.ProductRepository;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getAllCartItems() {
        return cartItemRepository.findAll();
    }

    public List<CartItem> getAllCartItemsByCart(Long customerId) {
        return cartRepository.findCartByUser_IdAndIsOrderedFalse(customerId).getCartItems();
    }

    @Transactional
    public List<CartItem> addProductToCart(Long productId, Long customerId) {
        Cart cart = cartRepository.findCartByUser_IdAndIsOrderedFalse(customerId);
        Product newProduct = getProductById(productId);
        List<CartItem> cartItems = cart.getCartItems();

        Optional<CartItem> existingCartItem = getFirstExistingCartItem(newProduct, cartItems);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = new CartItem(newProduct, 1);
            cartItems.add(newCartItem);
        }
        cartRepository.save(cart);
        BigDecimal totalAmount = getTotalAmount(cartItems);
        cart.setTotalAmount(totalAmount);

        return cartItems;
    }

    private Optional<CartItem> getFirstExistingCartItem(Product newProduct, List<CartItem> cartItems) {
        return cartItems
                .stream()
                .filter(cartItem -> cartItem.getProduct().equals(newProduct))
                .findFirst();
    }

    @Transactional
    public List<CartItem> decreaseProductQuantityInCart(Long productId, Long customerId) {
        Cart cart = cartRepository.findCartByUser_IdAndIsOrderedFalse(customerId);
        Product product = getProductById(productId);
        List<CartItem> cartItems = cart.getCartItems();

        Optional<CartItem> existingCartItem =
                getFirstExistingCartItem(product, cartItems);
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity > 0) {
                cartItem.setQuantity(newQuantity);
                cartItemRepository.save(cartItem);
            } else {
                cartItems.remove(cartItem);
                cartItemRepository.delete(cartItem);
            }
        } else {
            throw new ResourceNotFoundException("Product not found in the cart");
        }

        cartRepository.save(cart);
        BigDecimal totalAmount = getTotalAmount(cartItems);
        cart.setTotalAmount(totalAmount);

        return cartItems;
    }

    private Product getProductById(Long productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }


    @Transactional
    public List<CartItem> updateCart(Long customerId, List<CartItem> newCartItems) {

        Cart cart = cartRepository.findCartByUser_IdAndIsOrderedFalse(customerId);
        cart.getCartItems().clear();
        cart.getCartItems().addAll(newCartItems);
        cartRepository.save(cart);

        return cart.getCartItems();
    }

    @Transactional
    public List<CartItem> clearCart(Long customerId) {

        Cart cart = cartRepository.findCartByUser_IdAndIsOrderedFalse(customerId);
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return cart.getCartItems();
    }

    @Transactional
    public List<CartItem> removeProductFromCart(Long customerId, Long productId) {
        Cart cart = cartRepository.findCartByUser_IdAndIsOrderedFalse(customerId);
        Product product = getProductById(productId);
        List<CartItem> cartItems = cart.getCartItems();

        cartItems.removeIf(cartItem -> cartItem.getProduct().equals(product));
        cartRepository.save(cart);

        BigDecimal updatedTotalAmount = getTotalAmount(cartItems);
        cart.setTotalAmount(updatedTotalAmount);
        cartRepository.save(cart);

        return cart.getCartItems();
    }

    @Transactional
    public BigDecimal getTotalAmount(Long customerId) {
        Cart cart = cartRepository.findCartByUser_IdAndIsOrderedFalse(customerId);
        List<CartItem> cartItems = cart.getCartItems();

        return getTotalAmount(cartItems);
    }

    private static BigDecimal getTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                            Product product = cartItem.getProduct();
                            Integer quantity = cartItem.getQuantity();
                            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
                        })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
