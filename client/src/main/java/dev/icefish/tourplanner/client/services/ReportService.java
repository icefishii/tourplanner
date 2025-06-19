package dev.icefish.tourplanner.client.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.LineSeparator;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Image;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Chunk;
import dev.icefish.tourplanner.models.exceptions.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ReportService {
    private Logger logger = LogManager.getLogger(ReportService.class);
    public void generateTourReport(Tour tour, List<TourLog> tourLogs, Path filePath) {

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
            document.open();

            // Title
            Paragraph title = new Paragraph("Tour Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new LineSeparator(0.5f, 100, null, Element.ALIGN_CENTER, -2));
            document.add(Chunk.NEWLINE);

            // Tour Information Section
            addUnderlinedSectionHeader(document, "Tour Information");

            document.add(new Paragraph("Name: " + tour.getName()));
            document.add(new Paragraph("From: " + tour.getFromLocation()));
            document.add(new Paragraph("To: " + tour.getToLocation()));
            document.add(new Paragraph("Transport Type: " + tour.getTransportType()));
            document.add(new Paragraph(String.format("Distance: %.2f km", tour.getDistance())));
            document.add(new Paragraph(String.format("Estimated Time: %.2f h", tour.getEstimatedTime())));
            document.add(new Paragraph("Description: " + tour.getDescription()));

            document.add(Chunk.NEWLINE);

            // Add tour image
            try {
                String basePath = ConfigLoader.get("image.basePath");
                String imagePath = basePath + "/" + tour.getId() + ".png";

                Image tourImage = Image.getInstance(imagePath);
                tourImage.scaleToFit(400, 300);
                tourImage.setAlignment(Element.ALIGN_CENTER);
                document.add(tourImage);
                document.add(Chunk.NEWLINE);
            } catch (Exception e) {
                logger.warn("Tour image not found or could not be loaded: {}", e.getMessage());

            }

            // Tour Logs Section
            addUnderlinedSectionHeader(document, "Tour Logs");

            if (tourLogs.isEmpty()) {
                document.add(new Paragraph("No logs available."));
            } else {
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2f, 4f, 1.5f, 1.5f, 2f, 1.5f});

                // Headers
                Stream.of("Date", "Comment", "Difficulty", "Distance (km)", "Duration", "Rating")
                        .map(header -> new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)))
                        .forEach(table::addCell);

                for (TourLog log : tourLogs) {
                    table.addCell(log.getDate() != null ? log.getDate().toString() : "N/A");
                    table.addCell(log.getComment() != null ? log.getComment() : "");
                    table.addCell(String.valueOf(log.getDifficulty()));
                    table.addCell(String.format("%.2f", log.getDistance()));
                    table.addCell(log.getDurationText() != null ? log.getDurationText() : "");
                    table.addCell(String.valueOf(log.getRating()));
                }
                document.add(table);
            }

            document.close();
            logger.info("Report generated at: {}", filePath);

        } catch (Exception e) {
            logger.error("Error generating report: {}", e.getMessage());
            throw new ServiceException("Error generating report: " + e.getMessage());
        }
    }

    public void generateSummaryReport(Map<Tour, List<TourLog>> logsByTour, Path filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
            document.open();

            Paragraph title = new Paragraph("Summary Report - Statistical Analysis", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new LineSeparator(0.5f, 100, null, Element.ALIGN_CENTER, -2));
            document.add(Chunk.NEWLINE);

            if (logsByTour.isEmpty()) {
                document.add(new Paragraph("No tours available for summary."));
            } else {
                for (Map.Entry<Tour, List<TourLog>> entry : logsByTour.entrySet()) {
                    Tour tour = entry.getKey();
                    List<TourLog> tourLogs = entry.getValue();

                    addUnderlinedSectionHeader(document, "Tour: " + tour.getName());

                    try {
                        String basePath = ConfigLoader.get("image.basePath");
                        String imagePath = basePath + "/" + tour.getId() + ".png";

                        Image tourImage = Image.getInstance(imagePath);
                        tourImage.scaleToFit(400, 300);
                        tourImage.setAlignment(Element.ALIGN_CENTER);
                        document.add(tourImage);
                        document.add(Chunk.NEWLINE);
                    } catch (Exception e) {
                        logger.warn("Tour image not found or could not be loaded: {}", e.getMessage());
                    }

                    if (tourLogs.isEmpty()) {
                        document.add(new Paragraph("No tour logs available."));
                    } else {
                        double avgDistance = tourLogs.stream().mapToDouble(TourLog::getDistance).average().orElse(0);
                        double avgTime = tourLogs.stream()
                                .mapToDouble(log -> parseDurationToHours(log.getDurationText()))
                                .average().orElse(0);
                        double avgRating = tourLogs.stream().mapToInt(TourLog::getRating).average().orElse(0);

                        document.add(new Paragraph(String.format("Average Distance: %.2f km", avgDistance)));
                        document.add(new Paragraph(String.format("Average Duration: %.2f hours", avgTime)));
                        document.add(new Paragraph(String.format("Average Rating: %.2f / 5", avgRating)));
                    }

                    document.add(Chunk.NEWLINE);
                }
            }

            document.close();
            logger.info("Summary report generated at: " + filePath);

        } catch (Exception e) {
            logger.error("Error generating summary report: {}", e.getMessage());
            throw new ServiceException("Error generating summary report: " + e.getMessage());
        }
    }

    private double parseDurationToHours(String durationText) {
        if (durationText == null || durationText.isEmpty()) return 0;
        try {
            int minutes = Integer.parseInt(durationText.trim());
            return minutes / 60.0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void addUnderlinedSectionHeader(Document doc, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        p.setSpacingBefore(10f);
        doc.add(p);
        doc.add(new LineSeparator(0.5f, 100, null, Element.ALIGN_LEFT, -2));
        doc.add(Chunk.NEWLINE);
    }
}