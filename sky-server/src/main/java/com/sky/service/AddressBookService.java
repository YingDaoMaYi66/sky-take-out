package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 查询当前用户的所有地址信息
     * @param addressBook 地址对象
     */
    List<AddressBook> list(AddressBook addressBook);
    /**
     * 新增地址
     * @param addressBook 地址对象
     */
    void save(AddressBook addressBook);
    /**
     * 根据ID查询地址
     * @param id 用户id
     */
    AddressBook getById(Long id);
    /**
     * 修改地址
     * @param addressBook 地址对象
     */
    void update(AddressBook addressBook);
    /**
     * 设置默认地址
     * @param addressBook 地址对象
     */
    void setDefault(AddressBook addressBook);
    /**
     * 根据id删除地址
     * @param id 地址id
     */
    void deleteById(Long id);
}
