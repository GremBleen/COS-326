from PyPDF2 import PdfReader, PdfWriter
from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import landscape
import io

# Step 1: Read the existing PDF
existing_pdf_path = "UML.pdf"
reader = PdfReader(existing_pdf_path)
writer = PdfWriter()

# Step 2: Create a new PDF with the text
packet = io.BytesIO()
# Extract width and height from the mediabox
page = reader.pages[0]
width = page.mediabox.width
height = page.mediabox.height
# Create a new PDF with the same width and height
can = canvas.Canvas(packet, pagesize=landscape((width, height)))
can.setFont("Helvetica", 30)
can.setFillColor("white")
can.drawString(10, 1040, "Graeme Harry Blain - u22625462")  # Adjust coordinates as needed
can.save()

# Move to the beginning of the StringIO buffer
packet.seek(0)
new_pdf = PdfReader(packet)

# Step 3: Merge the two PDFs
for page_num in range(len(reader.pages)):
    page = reader.pages[page_num]
    if page_num == 0:  # Only add text to the first page
        page.merge_page(new_pdf.pages[0])
    writer.add_page(page)

# Save the result
output_pdf_path = "UML_with_name.pdf"
with open(output_pdf_path, "wb") as output_pdf:
    writer.write(output_pdf)
