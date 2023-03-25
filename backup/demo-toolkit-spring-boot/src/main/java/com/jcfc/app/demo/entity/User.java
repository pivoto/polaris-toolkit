package com.jcfc.app.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author Qt
 * @version Mar 07, 2022
 * @since 1.8
 */
@TableName("user")
@Getter
@Setter
@ToString
public class User {
	@TableId
	private String id;
	private String name;
	private Integer age;
	private String email;
	private String firstName;
	@Version
	private Date version;
}
