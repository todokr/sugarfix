/*
 * Select CSV string value & run `sugarfix: Generate DSL from CSV`.
 */

import com.intellij.util.containers.headTail

ide.registerAction("SugarfixGenFromCsv", "sugarfix: Generate DSL from CSV") { e ->
    val editor = e.getRequiredData(CommonDataKeys.EDITOR)
    val project = e.getRequiredData(CommonDataKeys.PROJECT)
    val primaryCaret = editor.caretModel.primaryCaret
    val start = primaryCaret.selectionStart
    val end = primaryCaret.selectionEnd

    WriteCommandAction.runWriteCommandAction(project) {
        val document = editor.document
        val range = TextRange(start, end)
        val formatted = formatDSL(document.getText(range))
        document.replaceString(start, end, formatted)
    }
}

val csvDelimiter = ","
val dslDelimiter = "|"

fun formatDSL(raw: String): String {
    val matrix = raw.lines().map { line ->
        line.split(csvDelimiter).map { it.trim() }
    }
    val transposed = List(matrix.first().size) { i ->
        matrix.map { it[i] }
    }
    val colWidths = transposed.map { col -> col.maxOf { it.length } }
    val aligned = matrix.map { cols ->
        cols.zip(colWidths).joinToString(dslDelimiter) { (col, max) ->
            val wsSize = max - col.length
            " ${col}${" ".repeat(wsSize)} "
        }
    }
    val (header, body) = aligned.headTail()
    val headerLine = colWidths.joinToString(dslDelimiter) { "-".repeat(it + 2) }
    return (listOf(header, headerLine) + body).joinToString("\n")
}

fun <T> Sequence<T>.cycle(): Sequence<T> =
    generateSequence(this) { this }.flatten()
