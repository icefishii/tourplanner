package dev.icefish.tourplanner.client.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import dev.icefish.tourplanner.client.utils.ConfigLoader;
import dev.icefish.tourplanner.models.Tour;
import dev.icefish.tourplanner.models.TourLog;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Image;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Chunk;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ReportService {

    public void generateTourReport(Tour tour, List<TourLog> tourLogs, Path filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
            document.open();

            // Title
            Paragraph title = new Paragraph("Tour Report");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE);

            // Tour basic info
            document.add(new Paragraph("Name: " + tour.getName()));
            document.add(new Paragraph("From: " + tour.getFromLocation()));
            document.add(new Paragraph("To: " + tour.getToLocation()));
            document.add(new Paragraph("Transport Type: " + tour.getTransportType()));
            document.add(new Paragraph(String.format("Distance: %.2f km", tour.getDistance())));
            document.add(new Paragraph(String.format("Estimated Time: %.2f h", tour.getEstimatedTime())));
            document.add(new Paragraph("Description: " + tour.getDescription()));

            document.add(Chunk.NEWLINE);

            // Add tour image if exists
            try {
                String basePath = ConfigLoader.get("image.basePath");
                String imagePath = basePath + "/" + tour.getId() + ".png";

                Image tourImage = Image.getInstance(imagePath);
                tourImage.scaleToFit(400, 300);
                tourImage.setAlignment(Element.ALIGN_CENTER);
                document.add(tourImage);
                document.add(Chunk.NEWLINE);
            } catch (Exception e) {
                System.out.println("Tour image not found or could not be loaded: " + e.getMessage());
            }

            // Tour Logs header
            Paragraph logsHeader = new Paragraph("Tour Logs");
            logsHeader.setSpacingBefore(10);
            logsHeader.setSpacingAfter(5);
            logsHeader.setAlignment(Element.ALIGN_LEFT);
            document.add(logsHeader);

            if (tourLogs.isEmpty()) {
                document.add(new Paragraph("No logs available."));
            } else {
                // Table with headers: Date, Comment, Difficulty, Distance, Duration, Rating
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2f, 4f, 1.5f, 1.5f, 2f, 1.5f});

                // Headers
                table.addCell("Date");
                table.addCell("Comment");
                table.addCell("Difficulty");
                table.addCell("Distance (km)");
                table.addCell("Duration");
                table.addCell("Rating");

                // Rows
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
            System.out.println("Report generated at: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateSummaryReport(Map<Tour, List<TourLog>> logsByTour, Path filePath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
            document.open();

            // Title
            Paragraph title = new Paragraph("Summary Report - Statistical Analysis");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            if (logsByTour.isEmpty()) {
                document.add(new Paragraph("No tours available for summary."));
            } else {
                for (Map.Entry<Tour, List<TourLog>> entry : logsByTour.entrySet()) {
                    Tour tour = entry.getKey();
                    List<TourLog> tourLogs = entry.getValue();

                    // Tour title
                    Paragraph tourTitle = new Paragraph("Tour: " + tour.getName());
                    tourTitle.setSpacingBefore(10);
                    tourTitle.setSpacingAfter(5);
                    document.add(tourTitle);

                    // Tour image
                    try {
                        String basePath = ConfigLoader.get("image.basePath");
                        String imagePath = basePath + "/" + tour.getId() + ".png";

                        Image tourImage = Image.getInstance(imagePath);
                        tourImage.scaleToFit(400, 300);
                        tourImage.setAlignment(Element.ALIGN_CENTER);
                        document.add(tourImage);
                        document.add(Chunk.NEWLINE);
                    } catch (Exception e) {
                        System.out.println("Tour image not found or could not be loaded: " + e.getMessage());
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
            System.out.println("Summary report generated at: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
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
}
