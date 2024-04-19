package com.app.gizmophile.service;

import com.app.gizmophile.dto.*;
import com.app.gizmophile.enumtype.Role;
import com.app.gizmophile.enumtype.Status;
import com.app.gizmophile.exception.StockNotAvailableException;
import com.app.gizmophile.model.*;
import com.app.gizmophile.repository.AddressRepository;
import com.app.gizmophile.repository.ProductRepository;
import com.app.gizmophile.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Value("${app.image.path}")
    private String imagePath;

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, AddressRepository addressRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    //User Profile
    public boolean checkUserExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean updatePassword(UpdatePassword data) {
        User existingUser = userRepository.findByEmail(data.getEmail()).orElseThrow();
        existingUser.setPassword(passwordEncoder.encode(data.getPassword()));
        try {
            userRepository.save(existingUser);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public UserData addUser(NewUser data) {
        User newUser = User.builder()
                .username(data.getUsername())
                .password(passwordEncoder.encode(data.getPassword()))
                .firstname(data.getFirstname())
                .lastname(data.getLastname())
                .email(data.getEmail())
                .role(Role.BASE)
                .build();

        User user = userRepository.save(newUser);
        return UserData.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .build();
    }

    public UserData getUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return UserData.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profile(user.getProfileImage())
                .dateOfBirth(user.getDateOfBirth())
                .defaultAddress(user.getDefaultAddress())
                .orders(user.getOrders().size())
                .reviews(user.getReviews().size())
                .addresses(user.getAddresses().size())
                .cartItems(user.getCartItems())
                .wishlistItems(user.getWishlistItems())
                .browsedItems(user.getBrowsedItems())
                .build();
    }

    public UserData updateUser(String username, UserInfo data) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if (data.getFirstname() != null) {
            user.setFirstname(data.getFirstname());
        }
        if (data.getLastname() != null) {
            user.setLastname(data.getLastname());
        }
        if (data.getDateOfBirth() != null) {
            user.setDateOfBirth(data.getDateOfBirth());
        }
        if (data.getEmail() != null) {
            user.setEmail(data.getEmail());
        }
        if (data.getPhone() != null) {
            user.setPhone(data.getPhone());
        }
        User update = userRepository.save(user);
        return UserData.builder()
                .id(update.getId())
                .username(update.getUsername())
                .firstname(update.getFirstname())
                .lastname(update.getLastname())
                .email(update.getEmail())
                .phone(update.getPhone())
                .profile(update.getProfileImage())
                .dateOfBirth(update.getDateOfBirth())
                .defaultAddress(update.getDefaultAddress())
                .orders(update.getOrders().size())
                .reviews(update.getAddresses().size())
                .addresses(update.getAddresses().size())
                .cartItems(update.getCartItems())
                .wishlistItems(update.getWishlistItems())
                .browsedItems(update.getBrowsedItems())
                .build();

    }

    public boolean updateProfile(MultipartFile image, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        if (!image.isEmpty()) {
            try {
                String originalFilename = image.getOriginalFilename();
//                    String uniqueFilename = generateUniqueFilename(originalFilename);
                Path profileDir = Paths.get(imagePath, "user-" + username);
                assert originalFilename != null;
                Path filePath = profileDir.resolve(originalFilename);

                Files.createDirectories(profileDir);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                user.setProfileImage(originalFilename);
                userRepository.save(user);
                return true;
            } catch (IOException e) {
                return false;
//                throw new RuntimeException("Failed to save image", e);
            }

        }
        return false;

    }

    public Resource getImage(String username) throws MalformedURLException {
        User user = userRepository.findByUsername(username).orElseThrow();
        Path filePath = Paths.get(imagePath, "user-" + username + "/" + user.getProfileImage());
        System.out.println(filePath.getFileName());
        return new UrlResource(filePath.toUri());
    }

    public boolean deleteImage(String username) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            Path filePath = Paths.get(imagePath, "user-" + username).resolve(user.getProfileImage());
            user.setProfileImage(null);
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Orders
    @Transactional
    public Long placeOrder(String username, OrderData orderData) {

        User user = userRepository.findByUsername(username).orElseThrow();
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemData x : orderData.getItems()) {
            OrderItem item = OrderItem.builder()
                    .quantity(x.getQuantity())
                    .item(productRepository.findById(x.getItem()).orElseThrow())
                    .itemColor(x.getColor())
                    .itemVariant(x.getVariant())
                    .itemValue(x.getItemValue()).build();
            items.add(item);
        }
        Address address = user.getAddresses()
                .stream().filter(x -> Objects.equals(x.getId(), orderData.getShippingAddress()))
                .findFirst().orElseThrow();

        Order newOrder = Order.builder()
                .status(Status.PLACED)
                .orderDate(LocalDateTime.now())
                .deliveryCharge(orderData.getDeliveryCharge())
                .orderAmount(orderData.getOrderAmount())
                .items(items)
                .shippingAddress(address)
                .paymentMode(orderData.getPaymentMode())
                .orderNumber(generateOrderNumber(user))
                .user(user)
                .build();

        items.forEach(item -> item.setOrder(newOrder));
        orderData.getItems().forEach(
                orderItem -> {
                    Product product = productRepository.findById(orderItem.getItem()).orElseThrow();
                    int count = product.getStock() - orderItem.getQuantity();
                    if (count <= 0) {
                        throw new StockNotAvailableException(product.getBrand() + product.getModel());
                    }
                    product.setStock(product.getStock() - orderItem.getQuantity());
                    productRepository.save(product);

                }
        );

        user.getOrders().add(newOrder);
        Order order = userRepository.save(user).getOrders().getLast();
        return order.getId();
    }

    public List<OrderData> getOrders(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<Order> orders = user.getOrders();
        List<OrderData> orderDataList = new ArrayList<>();
        orders.forEach(order -> {
            List<OrderItemData> orderItemDataList = new ArrayList<>();
            for (OrderItem orderItem : order.getItems()) {
                boolean isReviewed = orderItem.getReview() != null
                        && (
                        (orderItem.getReview().getContent() != null
                                && !orderItem.getReview().getContent().isBlank()
                        )
                                || !orderItem.getReview().getHighlights().isEmpty()
                                || !orderItem.getReview().getImages().isEmpty());

                OrderItemData orderItemData = OrderItemData.builder()
                        .id(orderItem.getId())
                        .item(orderItem.getItem().getId())
                        .type(orderItem.getItem().getType())
                        .itemName(orderItem.getItem().getBrand() + " " + orderItem.getItem().getModel())
                        .color(orderItem.getItemColor())
                        .variant(orderItem.getItemVariant())
                        .price(orderItem.getItemValue())
                        .itemValue(orderItem.getItemValue())
                        .quantity(orderItem.getQuantity())
                        .returnedQuantity(orderItem.getReturnedQuantity())
                        .replacedQuantity(orderItem.getReplacedQuantity())
                        .returnReason(orderItem.getReturnReason())
                        .replaceReason(orderItem.getReplaceReason())
                        .rating(orderItem.getReview() != null && orderItem.getReview().getRating() != null ? orderItem.getReview().getRating() : 0)
                        .imageUrl(orderItem.getItem().getColors().stream()
                                .filter(productColor -> Objects.equals(productColor.getColor(), orderItem.getItemColor()))
                                .findFirst().orElseThrow().getImage())
                        .isReviewed(isReviewed)
                        .reviewId(orderItem.getReview() != null ? orderItem.getReview().getId() : null)
                        .build();
                orderItemDataList.add(orderItemData);
            }
            var orderDTO = OrderData.builder()
                    .id(order.getId())
                    .orderId(order.getOrderNumber())
                    .deliveryCharge(order.getDeliveryCharge())
                    .orderAmount(order.getOrderAmount())
                    .orderDate(order.getOrderDate())
                    .shippingAddress(order.getShippingAddress().getId())
                    .paymentMode(order.getPaymentMode())
                    .status(order.getStatus().name())
                    .items(orderItemDataList)
                    .deliveryDate(order.getDeliveryDate())
                    .feedbackType(order.getFeedbackType())
                    .feedback(order.getFeedback())
                    .build();
            orderDataList.add(orderDTO);
        });
        return orderDataList;
    }

    public OrderData getOrder(String username, Long id) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Order order = user.getOrders().stream().filter(x -> Objects.equals(x.getId(), id)).findFirst().orElseThrow();

        List<OrderItemData> orderItemDataList = new ArrayList<>();
        for (OrderItem orderItem : order.getItems()) {
            boolean isReviewed = orderItem.getReview() != null
                    && (
                    (orderItem.getReview().getContent() != null
                            && !orderItem.getReview().getContent().isBlank()
                    )
                            || !orderItem.getReview().getHighlights().isEmpty()
                            || !orderItem.getReview().getImages().isEmpty());

            OrderItemData orderItemData = OrderItemData.builder().itemValue(orderItem.getItemValue())
                    .id(orderItem.getId())
                    .item(orderItem.getItem().getId())
                    .type(orderItem.getItem().getType())
                    .itemName(orderItem.getItem().getBrand() + " " + orderItem.getItem().getModel())
                    .imageUrl(orderItem.getItem().getColors().stream()
                            .filter(productColor -> Objects.equals(productColor.getColor(), orderItem.getItemColor()))
                            .findFirst().orElseThrow().getImage())
                    .color(orderItem.getItemColor())
                    .variant(orderItem.getItemVariant())
                    .price(orderItem.getItemValue())
                    .quantity(orderItem.getQuantity())
                    .returnedQuantity(orderItem.getReturnedQuantity())
                    .replacedQuantity(orderItem.getReplacedQuantity())
                    .returnReason(orderItem.getReturnReason())
                    .replaceReason(orderItem.getReplaceReason())
                    .itemValue(orderItem.getItemValue())
                    .rating(orderItem.getReview() != null && orderItem.getReview().getRating() != null ? orderItem.getReview().getRating() : 0)
                    .isReviewed(isReviewed)
                    .reviewId(orderItem.getReview() != null ? orderItem.getReview().getId() : null)
                    .build();
            orderItemDataList.add(orderItemData);
        }
        return OrderData.builder()
                .id(order.getId())
                .orderId(order.getOrderNumber())
                .deliveryCharge(order.getDeliveryCharge())
                .orderAmount(order.getOrderAmount())
                .orderDate(order.getOrderDate())
                .shippingAddress(order.getShippingAddress().getId())
                .paymentMode(order.getPaymentMode())
                .status(order.getStatus().name())
                .items(orderItemDataList)
                .deliveryDate(order.getDeliveryDate())
                .feedbackType(order.getFeedbackType())
                .feedback(order.getFeedback())
                .build();
    }

    public boolean returnOrReplaceItems(String username, Long orderId, String type, List<ReturnOrReplaceData> items) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            Order order = user.getOrders().stream().filter(x -> Objects.equals(x.getId(), orderId)).findFirst().orElseThrow();
            Map<Long, ReturnOrReplaceData> itemsMap = items.stream().collect(Collectors.toMap(ReturnOrReplaceData::getItemId, Function.identity()));

            if (Objects.equals(type, "return")) {
                order.getItems().forEach(item -> {
                    if (itemsMap.containsKey(item.getId())) {
                        var data = itemsMap.get(item.getId());
                        item.setReturnedQuantity(data.getQuantity());
                        item.setReturnReason(data.getReason());
                    }
                });
            } else {
                order.getItems().forEach(item -> {
                    if (itemsMap.containsKey(item.getId())) {
                        var data = itemsMap.get(item.getId());
                        item.setReplacedQuantity(data.getQuantity());
                        item.setReplaceReason(data.getReason());
                    }
                });
            }

            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void cancelOrder(String username, Long orderId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Order order = user.getOrders().stream().filter(x -> Objects.equals(x.getId(), orderId)).findFirst().orElseThrow();
        order.setStatus(Status.CANCELED);
        order.setDeliveryDate(LocalDateTime.now());
        userRepository.save(user);
    }

    // Update feedback
    public boolean updateFeedback(String username, Long orderId, Feedback fb) {
        try {

            User user = userRepository.findByUsername(username).orElseThrow();
            Order order = user.getOrders().stream().filter(x -> Objects.equals(x.getId(), orderId)).findFirst().orElseThrow();
            if (fb.getFeedback() != null) {
                order.setFeedback(fb.getFeedback());
            }
            if (fb.getFeedbackType() != null) {
                order.setFeedbackType(fb.getFeedbackType());
            }

            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    //Addresses
    public List<AddressData> getAllAddresses(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<AddressData> addressDataList = new ArrayList<>();
        user.getAddresses().forEach(address -> {
            AddressData data = modelMapper.map(address, AddressData.class);
            data.setIsDefault(Objects.equals(user.getDefaultAddress(), address.getId()));
            addressDataList.add(data);
        });
        return addressDataList;
    }

    public AddressData getAddress(String username, Long addressId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Address existingAddress = user.getAddresses().stream()
                .filter(address -> Objects.equals(address.getId(), addressId))
                .findFirst().orElseThrow();
        AddressData addressData = modelMapper.map(existingAddress, AddressData.class);
        addressData.setIsDefault(Objects.equals(user.getDefaultAddress(), existingAddress.getId()));
        return addressData;
    }

    public AddressData addAddress(String username, AddressData addressData) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Address address = modelMapper.map(addressData, Address.class);
        user.getAddresses().add(address);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        AddressData mappedAddress = modelMapper.map(savedAddress, AddressData.class);

        if (addressData.getIsDefault() != null & Boolean.TRUE.equals(addressData.getIsDefault())) {
            user.setDefaultAddress(savedAddress.getId());
            userRepository.save(user);
            mappedAddress.setIsDefault(true);
        }
        return mappedAddress;
    }

    public AddressData updateAddress(String username, AddressData addressData) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Address existingAddress = user.getAddresses().stream().filter(address -> Objects.equals(address.getId(), addressData.getId())).findFirst().orElseThrow();
        modelMapper.map(addressData, existingAddress);

        Address savedAddress = addressRepository.save(existingAddress);

        AddressData mappedAddress = modelMapper.map(savedAddress, AddressData.class);

        if (addressData.getIsDefault() != null & Boolean.TRUE.equals(addressData.getIsDefault())) {
            user.setDefaultAddress(existingAddress.getId());
            userRepository.save(user);
            mappedAddress.setIsDefault(true);
        }
        return mappedAddress;

    }

    public boolean deleteAddress(String username, Long addressId) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            Address existingAddress = addressRepository.findById(addressId).orElseThrow();
            user.getAddresses().removeIf(address -> Objects.equals(address.getId(), existingAddress.getId()));
            if (Objects.equals(user.getDefaultAddress(), addressId)) {
                user.setDefaultAddress(null);
            }
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public AddressData setDefaultAddress(String username, Long addressId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        boolean addressExists = user.getAddresses().stream().anyMatch(address -> Objects.equals(address.getId(), addressId));
        if (addressExists) {
            user.setDefaultAddress(addressId);
        }
        userRepository.save(user);
        Address existingAddress = addressRepository.findById(addressId).orElseThrow();
        AddressData mappedAddress = modelMapper.map(existingAddress, AddressData.class);
        mappedAddress.setIsDefault(true);
        return mappedAddress;
    }


    // App data
    public List<String> getAllItems(String username, String itemType) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return switch (itemType) {
            case "cartItem" -> user.getCartItems();
            case "wishlistItem" -> user.getWishlistItems();
            case "browsedItem" -> user.getBrowsedItems();
            default -> throw new IllegalStateException("Unexpected value: " + itemType);
        };

    }

    public void addItem(String username, String itemType, String item) {
        User user = userRepository.findByUsername(username).orElseThrow();
        System.out.println("uid "+user.getId());
        System.out.println("uname "+user.getUsername());
        switch (itemType) {
            case "cartItem" -> user.getCartItems().add(item);
            case "wishlistItem" -> user.getWishlistItems().add(item);
            case "browsedItem" -> {
                var size = user.getBrowsedItems().size();
                if (size == 8) {
                    user.getBrowsedItems().removeFirst();
                }
                user.getBrowsedItems().add(item);
            }
            default -> throw new IllegalStateException("Unexpected value: " + itemType);
        }
        userRepository.save(user);
    }

    public void updateItem(String username, String itemType, UpdateUserItem item) {
        User user = userRepository.findByUsername(username).orElseThrow();
        switch (itemType) {
            case "cartItem" -> {
                System.out.println(user.getCartItems());
                user.getCartItems().remove(item.getPreviousValue());
                System.out.println(user.getCartItems());
                user.getCartItems().add(item.getCurrentValue());
                System.out.println(user.getCartItems());
            }
            case "wishlistItem" -> {
                user.getWishlistItems().remove(item.getPreviousValue());
                user.getWishlistItems().add(item.getCurrentValue());
            }
            case "browsedItem" -> {
                user.getBrowsedItems().remove(item.getPreviousValue());
                user.getBrowsedItems().add(item.getCurrentValue());
            }
            default -> throw new IllegalStateException("Unexpected value: " + itemType);
        }
        userRepository.save(user);
    }

    public void deleteItem(String username, String itemType, String item) {
        User user = userRepository.findByUsername(username).orElseThrow();
        switch (itemType) {
            case "cartItem" -> user.getCartItems().remove(item);
            case "wishlistItem" -> user.getWishlistItems().remove(item);
            case "browsedItem" -> user.getBrowsedItems().remove(item);
            default -> throw new IllegalStateException("Unexpected value: " + itemType);
        }
        userRepository.save(user);
    }

    public void deleteAllItems(String username, String itemType) {
        User user = userRepository.findByUsername(username).orElseThrow();
        switch (itemType) {
            case "cartItem" -> user.getCartItems().clear();
            case "wishlistItem" -> user.getWishlistItems().clear();
            case "browsedItem" -> user.getBrowsedItems().clear();
            default -> throw new IllegalStateException("Unexpected value: " + itemType);
        }
        userRepository.save(user);
    }

    private String generateOrderNumber(User user) {

        var arr = user.getUsername().toCharArray();
        StringBuilder leftStringBuilder = new StringBuilder();

        for (char c : arr) {
            leftStringBuilder.append((int) c);
        }
        String leftString = leftStringBuilder.toString();

        String count = String.valueOf(user.getOrders().size() + 1);
        String middleString = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        String rightString = LocalTime.now().format(DateTimeFormatter.ofPattern("hhmm"))
                + "0".repeat(Math.max(0, 3 - count.length())) + count;

        return String.format("%S-%S-%S", leftString, middleString, rightString);
    }
}
