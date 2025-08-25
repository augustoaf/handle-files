# PowerShell script to create a text file with 50k lines.
# Define the number of lines to be generated.
$numLines = 50000

# Define the output file path. You can change this to a different location.
$outputFile = ".\input.txt"

Write-Host "Starting to generate file: $outputFile"

# Use a For loop to iterate and create the lines.
# This approach is memory-efficient for very large numbers of lines.
$content = for ($i = 1; $i -le $numLines; $i++) {
    "test$i"
}

# Use Set-Content to write the array of lines to the file.
# This will overwrite the file if it already exists.
try {
    $content | Set-Content -Path $outputFile -Encoding UTF8
    Write-Host "Successfully created '$outputFile' with $numLines lines."
} catch {
    # Catch any potential errors during file writing and display them.
    Write-Host "An error occurred while writing the file."
    Write-Host "Error details: $_"
}