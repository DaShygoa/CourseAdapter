
package main.java.parser

import bean.Course
import parser.Parser
import main.java.bean.TimeTable
import main.java.bean.TimeDetail

class SZPUParser(source: String) : Parser(source) {
    private val MAX_NODES_PER_DAY = 13
    // SZPU 课表时间（如有不同可调整）
    private val timeDetails = listOf(
        TimeDetail(1, "08:20", "09:00"),
        TimeDetail(2, "09:10", "09:50"),
        TimeDetail(3, "10:10", "10:50"),
        TimeDetail(4, "11:00", "11:40"),
        TimeDetail(5, "12:00", "12:40"),
        TimeDetail(6, "13:30", "14:10"),
        TimeDetail(7, "14:20", "15:00"),
        TimeDetail(8, "15:10", "15:50"),
        TimeDetail(9, "16:00", "16:40"),
        TimeDetail(10, "17:00", "17:40"),
        TimeDetail(11, "18:30", "19:10"),
        TimeDetail(12, "19:20", "20:00"),
        TimeDetail(13, "20:10", "20:50")
    )

    override fun generateCourseList(): List<Course> {
        val courseList = arrayListOf<Course>()
        // 支持 txt 纯文本格式：每行一课，字段用制表符或空格分隔
        val lines = source.lines().filter { it.isNotBlank() }
        for (line in lines) {
            // 例：课程名	星期	节次	周次	教室	教师
            val parts = line.split('\t', ' ', '，', ',').filter { it.isNotBlank() }
            if (parts.size < 6) continue
            val name = parts[0]
            val day = parseDay(parts[1])
            val (startNode, endNode) = parseNodes(parts[2])
            val (startWeek, endWeek, type) = parseWeeks(parts[3])
            val room = parts[4]
            val teacher = parts[5]
            courseList.add(
                Course(
                    name = name,
                    day = day,
                    room = room,
                    teacher = teacher,
                    startNode = startNode,
                    endNode = endNode,
                    startWeek = startWeek,
                    endWeek = endWeek,
                    type = type,
                )
            )
        }
        return mergeAdjacentCourses(courseList)
    }

    private fun parseDay(dayStr: String): Int {
        return when (dayStr.trim()) {
            "一", "1", "Mon", "Monday" -> 1
            "二", "2", "Tue", "Tuesday" -> 2
            "三", "3", "Wed", "Wednesday" -> 3
            "四", "4", "Thu", "Thursday" -> 4
            "五", "5", "Fri", "Friday" -> 5
            "六", "6", "Sat", "Saturday" -> 6
            "日", "天", "7", "Sun", "Sunday" -> 7
            else -> 1
        }
    }

    private fun parseNodes(nodeStr: String): Pair<Int, Int> {
        // 例：1-2节、3-4、5节
        val regex = Regex("(\\d+)(?:-(\\d+))?")
        val match = regex.find(nodeStr)
        return if (match != null) {
            val start = match.groupValues[1].toInt()
            val end = match.groupValues.getOrNull(2)?.toIntOrNull() ?: start
            Pair(start, end)
        } else Pair(1, 1)
    }

    private fun mergeAdjacentCourses(courses: List<Course>): List<Course> {
        val sorted = courses.sortedWith(compareBy({ it.day }, { it.startNode }))
        val merged = mutableListOf<Course>()
        for (current in sorted) {
            val last = merged.lastOrNull()
            if (last != null && last.day == current.day && last.name == current.name && last.endNode + 1 == current.startNode && last.startWeek == current.startWeek && last.endWeek == current.endWeek && last.type == current.type) {
                merged[merged.lastIndex] = last.copy(endNode = current.endNode)
            } else {
                merged.add(current)
            }
        }
        return merged
    }

  private fun parseWeeks(weekText: String): Triple<Int, Int, Int> {
    // 例：1-16周、1-16周(单)、1-16周(双)
    val regex = Regex("(\\d+)-(\\d+)周(?:\\((单|双)\\))?")
    val match = regex.find(weekText)
    if (match != null) {
      val startWeek = match.groupValues[1].toInt()
      val endWeek = match.groupValues[2].toInt()
      val type = when (match.groupValues.getOrNull(3)) {
        "单" -> 1
        "双" -> 2
        else -> 0
      }
      return Triple(startWeek, endWeek, type)
    }
    return Triple(1, 20, 0)
  }

  override fun generateTimeTable(): TimeTable? {
    val beanTimeDetails = timeDetails
    return TimeTable("SZPU标准作息", beanTimeDetails)
  }

  override fun getTableName(): String? = "深圳职业技术大学课表"
  override fun getNodes(): Int? = timeDetails.size
  override fun getStartDate(): String? = null
  override fun getMaxWeek(): Int? = 20
}

