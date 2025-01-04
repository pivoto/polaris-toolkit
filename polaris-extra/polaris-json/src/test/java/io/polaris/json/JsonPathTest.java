package io.polaris.json;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.JsonPath;

/**
 * @author Qt
 * @since Jan 02, 2025
 */
public class JsonPathTest {

	@Test
	void test01() {
		// 示例JSON字符串
		String json = "{\"store\": {" +
			"  \"book\": [" +
			"    {\"category\": \"reference\", \"author\": \"Nigel Rees\", \"title\": \"Sayings of the Century\", \"price\": 8.95}," +
			"    {\"category\": \"fiction\", \"author\": \"Evelyn Waugh\", \"title\": \"Sword of Honour\", \"price\": 12.99}," +
			"    {\"category\": \"fiction\", \"author\": \"Herman Melville\", \"title\": \"Moby Dick\", \"isbn\": \"0-553-21311-3\", \"price\": 8.99}," +
			"    {\"category\": \"fiction\", \"author\": \"J. R. R. Tolkien\", \"title\": \"The Lord of the Rings\", \"isbn\": \"0-395-19395-8\", \"price\": 22.99}" +
			"  ]," +
			"  \"bicycle\": {" +
			"    \"color\": \"red\", \"price\": 19.95" +
			"  }" +
			"}}";

		Object jsonNode = Jacksons.toJavaObject(json, Map.class);
//		Object jsonNode = Jacksons.toJsonTree(json);


		// 查询所有书籍的标题
		String bookTitlesJsonPath = "$.store.book[*].title";
		Object bookTitles = JsonPath.read(jsonNode, bookTitlesJsonPath);
		System.out.println("所有书籍的标题: " + bookTitles);

		// 查询价格大于10的书籍
		String expensiveBooksJsonPath = "$.store.book[?(@.price > 10)]";
		Object expensiveBooks = JsonPath.read(jsonNode, expensiveBooksJsonPath);
		System.out.println("价格大于10的书籍: " + expensiveBooks);
	}
}
