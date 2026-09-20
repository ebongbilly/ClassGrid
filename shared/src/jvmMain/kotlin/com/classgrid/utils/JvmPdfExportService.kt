package com.classgrid.utils

import com.classgrid.models.*
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import java.io.FileOutputStream
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.Desktop
import java.util.*

class JvmPdfExportService : PdfExportService {

    private fun getSavePath(defaultName: String): String? {
        val chooser = JFileChooser()
        chooser.dialogTitle = "Save PDF"
        chooser.fileFilter = FileNameExtensionFilter("PDF Files", "pdf")
        chooser.selectedFile = File(defaultName)
        val result = chooser.showSaveDialog(null)
        return if (result == JFileChooser.APPROVE_OPTION) {
            var path = chooser.selectedFile.absolutePath
            if (!path.endsWith(".pdf")) path += ".pdf"
            path
        } else null
    }

    override fun exportReportCard(
        school: School,
        student: Student,
        session: ExamSession,
        subjects: List<Subject>,
        marks: List<Mark>,
        overallRank: Int,
        classSize: Int,
        studentAverages: List<Pair<String, Double>>
    ) {
        val path = getSavePath("Report_${student.studentName.replace(" ", "_")}_${session.name}.pdf") ?: return
        
        val document = Document(PageSize.A4)
        try {
            PdfWriter.getInstance(document, FileOutputStream(path))
            document.open()

            // Header
            val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f)
            val header = Paragraph(school.name.uppercase(), titleFont)
            header.alignment = Element.ALIGN_CENTER
            document.add(header)

            val subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f)
            val subTitle = Paragraph("STUDENT PROGRESS REPORT CARD", subTitleFont)
            subTitle.alignment = Element.ALIGN_CENTER
            document.add(subTitle)

            document.add(Paragraph(" ")) // Spacer

            // Student Info
            val infoTable = PdfPTable(2)
            infoTable.widthPercentage = 100f
            infoTable.addCell(getCell("Student Name: ${student.studentName}", PdfPCell.NO_BORDER))
            infoTable.addCell(getCell("Class: ${student.className}", PdfPCell.NO_BORDER, Element.ALIGN_RIGHT))
            infoTable.addCell(getCell("Student ID: ${student.id}", PdfPCell.NO_BORDER))
            infoTable.addCell(getCell("Session: ${session.name}", PdfPCell.NO_BORDER, Element.ALIGN_RIGHT))
            document.add(infoTable)

            document.add(Paragraph(" "))

            // Marks Table
            val table = PdfPTable(6)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(3f, 1f, 1.2f, 0.8f, 1.2f, 0.8f))

            val headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f)
            table.addCell(PdfPCell(Phrase("Subject Name", headFont)))
            table.addCell(PdfPCell(Phrase("Code", headFont)))
            table.addCell(PdfPCell(Phrase("Score /20", headFont)))
            table.addCell(PdfPCell(Phrase("Coef", headFont)))
            table.addCell(PdfPCell(Phrase("Total", headFont)))
            table.addCell(PdfPCell(Phrase("Rank", headFont)))

            var totalWeighted = 0.0
            var totalCoefs = 0

            subjects.forEach { subject ->
                val mark = marks.find { it.subjectId == subject.id }
                val score = mark?.score ?: 0.0
                val weighted = score * subject.coefficient
                
                table.addCell(subject.name)
                table.addCell(subject.code)
                table.addCell(mark?.let { String.format("%.2f", it.score) } ?: "-")
                table.addCell(subject.coefficient.toString())
                table.addCell(mark?.let { String.format("%.2f", weighted) } ?: "-")
                
                // Simplified rank calculation for PDF (or pass it in)
                table.addCell("-") // Rank needs pre-calculation if passed individually

                if (mark != null) {
                    totalWeighted += weighted
                    totalCoefs += subject.coefficient
                }
            }
            document.add(table)

            document.add(Paragraph(" "))

            // Summary
            val average = if (totalCoefs > 0) totalWeighted / totalCoefs else 0.0
            val summary = Paragraph()
            summary.add(Chunk("Total Weighted Score: ${String.format("%.2f", totalWeighted)}\n"))
            summary.add(Chunk("Total Coefficients: $totalCoefs\n"))
            summary.add(Chunk("Student Average: ${String.format("%.2f", average)} / 20.0\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f)))
            summary.add(Chunk("Rank: $overallRank / $classSize", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f)))
            document.add(summary)

            document.close()
            Desktop.getDesktop().open(File(path))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun exportOrderOfMerit(
        school: School,
        session: ExamSession,
        academicClass: AcademicClass,
        meritList: List<MeritExportData>,
        subjects: List<Subject>
    ) {
        val path = getSavePath("MeritList_${academicClass.name.replace(" ", "_")}_${session.name}.pdf") ?: return
        
        val document = Document(PageSize.A4.rotate())
        try {
            PdfWriter.getInstance(document, FileOutputStream(path))
            document.open()

            val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f)
            val header = Paragraph("${school.name.uppercase()}\nORDER OF MERIT - ${academicClass.name} - ${session.name}", titleFont)
            header.alignment = Element.ALIGN_CENTER
            document.add(header)
            document.add(Paragraph(" "))

            val table = PdfPTable(subjects.size + 3)
            table.widthPercentage = 100f
            
            val headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f)
            table.addCell(PdfPCell(Phrase("Rank", headFont)))
            table.addCell(PdfPCell(Phrase("Student Name", headFont)))
            subjects.forEach { subject ->
                table.addCell(PdfPCell(Phrase("${subject.code}\n(${subject.coefficient})", headFont)))
            }
            table.addCell(PdfPCell(Phrase("Average", headFont)))

            meritList.forEachIndexed { index, item ->
                table.addCell((index + 1).toString())
                table.addCell(item.studentName)
                subjects.forEach { subject ->
                    val score = item.scores[subject.id]
                    table.addCell(score?.let { String.format("%.2f", it) } ?: "-")
                }
                table.addCell(String.format("%.2f", item.average))
            }

            document.add(table)
            document.close()
            Desktop.getDesktop().open(File(path))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCell(text: String, border: Int, alignment: Int = Element.ALIGN_LEFT): PdfPCell {
        val cell = PdfPCell(Phrase(text))
        cell.border = border
        cell.horizontalAlignment = alignment
        return cell
    }
}

actual fun getPdfExportService(): PdfExportService = JvmPdfExportService()
