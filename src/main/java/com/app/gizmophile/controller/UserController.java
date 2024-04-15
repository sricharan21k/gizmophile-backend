package com.app.gizmophile.controller;

import com.app.gizmophile.dto.*;
import com.app.gizmophile.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //User data
    @PatchMapping("/password")
    public boolean updatePassword(@RequestBody UpdatePassword data){
        return userService.updatePassword(data);
    }

    @PostMapping("register")
    public UserData newUser(@RequestBody NewUser userData){
        return userService.addUser(userData);
    }

    @GetMapping("/{username}")
    public UserData getUser(@PathVariable String username){
        return userService.getUser(username);
    }

    @PutMapping("/{username}")
    public UserData updateUser(@PathVariable String username, @RequestBody UserInfo userInfo){
        return userService.updateUser(username, userInfo);
    }

    @GetMapping("/{username}/profile-image")
    public Resource getImage(@PathVariable String username) {
        try {
            Resource image = userService.getImage(username);
            if (image.exists() || image.isReadable()) {
                return image;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{username}/profile-image")
    public String updateProfile(@RequestParam("profileImage") MultipartFile image, @PathVariable String username) {
        return userService.updateProfile(image, username);
    }

    @DeleteMapping("/{username}/profile-image")
    public boolean deleteImage(@PathVariable String username){
        return userService.deleteImage(username);
    }

    @GetMapping("/check/{username}")
    public boolean checkUserExists(@PathVariable String username){
        return userService.checkUserExists(username);
    }


    //Orders
    @GetMapping("/{username}/orders")
    public List<OrderData> getOrders(@PathVariable String username){
        return userService.getOrders(username);
    }

    @GetMapping("/{username}/order/{orderId}")
    public OrderData getOrder(@PathVariable String username, @PathVariable Long orderId){
        return userService.getOrder(username, orderId);

    }

    @PostMapping("/{username}/order")
    public Long placeOrder(@PathVariable String username, @RequestBody OrderData orderData){
        return userService.placeOrder(username, orderData);
    }

    @PatchMapping("/{username}/order/{orderId}/{type}")
    public boolean returnItems(@PathVariable String username,
                               @PathVariable Long orderId,
                               @PathVariable String type,
                               @RequestBody List<ReturnOrReplaceData> items){
        return userService.returnOrReplaceItems(username, orderId, type, items);

    }

    @PatchMapping("/{username}/order/{orderId}/cancel")
    public void cancelOrder(@PathVariable String username,
                            @PathVariable Long orderId){
        userService.cancelOrder(username, orderId);
    }

    @PatchMapping("/{username}/order/feedback/{orderId}")
    public boolean updateFeedback(@PathVariable String username, @PathVariable Long orderId, @RequestBody Feedback feedback){
        return userService.updateFeedback(username, orderId, feedback);
    }

    // Addresses
    @GetMapping("/{username}/addresses")
    public List<AddressData> getAddresses(@PathVariable String username){
        return userService.getAllAddresses(username);
    }

    @GetMapping("/{username}/address/{addressId}")
    public AddressData getAddress(@PathVariable String username, @PathVariable Long addressId){
        return userService.getAddress(username, addressId);
    }

    @PostMapping("/{username}/address")
    public AddressData addAddress(@PathVariable String username,
                                  @RequestBody AddressData addressdata){
        return userService.addAddress(username, addressdata);
    }

    @PutMapping("/{username}/address")
    public AddressData updateAddress(@PathVariable String username, @RequestBody AddressData addressdata){
        return userService.updateAddress(username, addressdata);
    }

    @PatchMapping("/{username}/address/{addressId}")
    public AddressData setDefaultAddress(@PathVariable String username, @PathVariable Long addressId){
        return userService.setDefaultAddress(username, addressId);
    }

    @DeleteMapping("/{username}/address/{addressId}")
    public boolean deleteAddress(@PathVariable String username, @PathVariable Long addressId){
        return userService.deleteAddress(username, addressId);
    }

    //App data
    @GetMapping("/{username}/data/{itemType}")
    public List<String> getAllItems(@PathVariable String username, @PathVariable String itemType){
        return userService.getAllItems(username, itemType);
    }

    @PostMapping("/{username}/data/{itemType}")
    public void addItem(@PathVariable String username, @PathVariable String itemType, @RequestBody String item){
        System.out.println("request body "+ item);
        userService.addItem(username, itemType, item);
    }

    @PutMapping("/{username}/data/{itemType}")
    public void updateItem(@PathVariable String username, @PathVariable String itemType, @RequestBody UpdateUserItem item){
        System.out.println("body "+ item);
        userService.updateItem(username, itemType, item);
    }

    @DeleteMapping("/{username}/data/{itemType}/{item}")
    public void deleteItem(@PathVariable String username, @PathVariable String itemType, @PathVariable String item){
        userService.deleteItem(username, itemType, item);
    }

    @DeleteMapping("/{username}/data/{itemType}")
    public void deleteAll(@PathVariable String username, @PathVariable String itemType){
        userService.deleteAllItems(username, itemType);
    }

}
