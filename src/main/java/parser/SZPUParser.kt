package test.java.parser

import main.java.parser.SZPUParser
import org.junit.Test

class SZPUParserTest {

    @Test
    fun testGenerateCourseList() {
        // 1. 准备模拟的 JSON 数据
        // 这是根据你的 provider.js 构建的示例数据
        val sampleJson = """
            {
              "success": true,
              "data": [
                {
                  "courseName": "大学英语（三）",
                  "teacher": "张三",
                  "time": "1-16周 星期一 第1节-第2节 智慧教室H3404",
                  "location": "智慧教室H3404",
                  "weeks": "1-16周",
                  "credit": "2.0"
                },
                {
                  "courseName": "高等数学（上）",
                  "teacher": "李四",
                  "time": "5周[单] 星期三 第3节-第4节 立言楼L1101,8-10周 星期三 第3节-第4节 立言楼L1101",
                  "location": "立言楼L1101",
                  "weeks": "5周[单],8-10周",
                  "credit": "4.0"
                },
                {
                  "courseName": "体育",
                  "teacher": "王五",
                  "time": "1-8周 星期五 第7节-第8节 西校区体育馆",
                  "location": "西校区体育馆",
                  "weeks": "1-8周",
                  "credit": "1.0"
                }
              ],
              "year": "2024",
              "term": "1"
            }
        """.trimIndent()

        // 2. 创建解析器实例
        val parser = SZPUParser(sampleJson)

        // 3. 调用解析方法
        val courseList = parser.generateCourseList()

        // 4. 打印解析结果进行验证
        println("解析到的课程数量: ${courseList.size}")
        courseList.forEach { course ->
            println("--------------------")
            println("课程名: ${course.name}")
            println("教师: ${course.teacher}")
            println("教室: ${course.room}")
            println("星期: ${course.day}")
            println("节次: ${course.startNode}-${course.endNode}")
            println("周次: ${course.startWeek}-${course.endWeek} (类型: ${course.type})")
        }

        // 也可以在这里添加断言来自动化验证
        // import org.junit.Assert.assertEquals
        // assertEquals(20, courseList.size) // 示例：期望解析出20个课程对象
    }

    @Test
    fun testGenerateTimeTable() {
        val parser = SZPUParser("{}") // 时间表生成与源无关，传入空JSON即可
        val timeTable = parser.generateTimeTable()
        println("\n时间表: ${timeTable.name}")
        timeTable.timeList.forEach {
            println("第${it.node}节: ${it.startTime}-${it.endTime}")
        }
    }
}

