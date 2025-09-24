$reportDir = "target/surefire-reports"
if (!(Test-Path $reportDir)) { Write-Host "Report directory not found: $reportDir"; exit 1 }

$totTests = 0
$totFail  = 0
$totErr   = 0
$totSkip  = 0

Get-ChildItem $reportDir -Filter 'TEST-*.xml' | ForEach-Object {
    [xml]$x = Get-Content -Raw $_.FullName
    $s = $x.testsuite
    if ($null -ne $s) {
        $totTests += [int]$s.tests
        $totFail  += [int]$s.failures
        $totErr   += [int]$s.errors
        $totSkip  += [int]$s.skipped
        Write-Host ("{0} tests={1} fail={2} err={3} skip={4}" -f $_.Name, $s.tests, $s.failures, $s.errors, $s.skipped)
    }
}
Write-Host ("TOTAL tests={0} fail={1} err={2} skip={3}" -f $totTests, $totFail, $totErr, $totSkip)
