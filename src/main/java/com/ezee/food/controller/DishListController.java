package com.ezee.food.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ezee.food.controller.io.*;
import com.ezee.food.dto.*;
import com.ezee.food.service.DishListService;

@RestController
@RequestMapping("/dishlist")
public class DishListController {

    @Autowired
    private DishListService dishList;

    @GetMapping("/")
    public ResponseIO<List<DishListIO>> getAllDishList(@RequestHeader("authCode") String authCode) {
        List<DishListDTO> allDishList = dishList.getAllDishList(authCode);
        List<DishListIO> responseList = new ArrayList<>();

        for (DishListDTO dto : allDishList) {
            responseList.add(mapToDishListIO(dto));
        }

        return ResponseIO.success(responseList);
    }

    @GetMapping("/{code}")
    public ResponseIO<DishListIO> getDishList(@PathVariable("code") String code,
                                              @RequestHeader("authCode") String authCode) {
        DishListDTO inputDTO = new DishListDTO();
        inputDTO.setCode(code);
        DishListDTO dto = dishList.getDishListByCode(inputDTO, authCode);
        return ResponseIO.success(mapToDishListIO(dto));
    }

 

    private DishListIO mapToDishListIO(DishListDTO dto) {
        DishListIO io = new DishListIO();
        io.setCode(dto.getCode());
        io.setUnitPrice(dto.getUnitPrice());

        if (dto.getMenuDTO() != null) {
            MenuIO menuIO = new MenuIO();
            menuIO.setCode(dto.getMenuDTO().getCode());
            menuIO.setName(dto.getName()); 
            io.setMenuIO(menuIO);
        }

        if (dto.getDishDTO() != null) {
            io.setDishIO(mapToDishIO(dto.getDishDTO()));
        }

        return io;
    }

    private DishIO mapToDishIO(DishDTO dto) {
        DishIO dishIO = new DishIO();
        dishIO.setCode(dto.getCode());
        dishIO.setName(dto.getName());
        dishIO.setDescription(dto.getDescription());
        dishIO.setTimeToMake(dto.getTimeToMake());
        dishIO.setVegType(dto.getVegType());
        dishIO.setMinAvailableQuantity(dto.getMinAvailableQuantity());
        dishIO.setServingSize(dto.getServingSize());
        dishIO.setMarginProfit(dto.getMarginProfit());
        dishIO.setPrice(dto.getPrice());

        if (dto.getTaxDTO() != null) {
            TaxIO taxIO = new TaxIO();
            taxIO.setCode(dto.getTaxDTO().getCode());
            taxIO.setDescription(dto.getTaxDTO().getDescription());
            taxIO.setRatePercentage(dto.getTaxDTO().getRatePercentage());
            dishIO.setTaxIO(taxIO);
        }

        List<DishIngredientIO> ingredients = new ArrayList<>();
        if (dto.getDishIngredientList() != null) {
            for (DishIngredientDTO ingDTO : dto.getDishIngredientList()) {
                DishIngredientIO ingIO = new DishIngredientIO();
                ingIO.setCode(ingDTO.getCode());

                if (ingDTO.getIngredientDTO() != null) {
                    IngredientIO ingredient = new IngredientIO();
                    ingredient.setCode(ingDTO.getIngredientDTO().getCode());
                    ingredient.setName(ingDTO.getIngredientDTO().getName());
                    ingIO.setIngredientIO(ingredient);
                }

                ingIO.setQunatityUsed(ingDTO.getQunatityUsed());
                ingIO.setWastage(ingDTO.getWastage());
                ingIO.setPrice(ingDTO.getPrice());

                ingredients.add(ingIO);
            }
        }
        dishIO.setDishIngredientIO(ingredients);

        List<DishLabourIO> labours = new ArrayList<>();
        if (dto.getDishLabourList() != null) {
            for (DishLabourDTO labDTO : dto.getDishLabourList()) {
                DishLabourIO labIO = new DishLabourIO();
                labIO.setCode(labDTO.getCode());

                if (labDTO.getLabourDTO() != null) {
                    LabourIO labour = new LabourIO();
                    labour.setCode(labDTO.getLabourDTO().getCode());
                    labour.setName(labDTO.getLabourDTO().getName());
                    labour.setRoleName(labDTO.getLabourDTO().getRoleName());
                    labour.setSpecialization(labDTO.getLabourDTO().getSpecialization());
                    labour.setHourslySalary(labDTO.getLabourDTO().getHourslySalary());
                    labIO.setLabourIO(labour);
                }

                labIO.setHoursRequired(labDTO.getHoursRequired());
                labours.add(labIO);
            }
        }
        dishIO.setDishLabourIO(labours);

        return dishIO;
    }
}
