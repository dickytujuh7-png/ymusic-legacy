import importlib.util
import tempfile
import unittest
import zipfile
from pathlib import Path

TOOL = Path(__file__).parents[1] / "tools" / "estimate_release_size.py"
SPEC = importlib.util.spec_from_file_location("estimate_release_size", TOOL)
MODULE = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(MODULE)


class EstimateReleaseSizeTests(unittest.TestCase):
    def make_archive(self, path):
        with zipfile.ZipFile(path, "w", compression=zipfile.ZIP_DEFLATED) as archive:
            archive.writestr("system.img", b"S" * 1000)
            archive.writestr("boot.img", b"B" * 100)

    def test_estimate_includes_reserve_and_checks_budget(self):
        with tempfile.TemporaryDirectory() as directory:
            archive = Path(directory) / "firmware.zip"
            self.make_archive(archive)
            result = MODULE.estimate(archive, ["system.img", "boot.img"], 1, 0.01)
            self.assertEqual(result["estimated_package_bytes"],
                             result["payload_compressed_bytes"] + result["metadata_reserve_bytes"])
            self.assertTrue(result["within_budget_estimate"])

    def test_missing_member_is_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            archive = Path(directory) / "firmware.zip"
            self.make_archive(archive)
            with self.assertRaisesRegex(ValueError, "missing requested"):
                MODULE.estimate(archive, ["recovery.img"], 200, 2)

    def test_negative_reserve_is_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            archive = Path(directory) / "firmware.zip"
            self.make_archive(archive)
            with self.assertRaises(ValueError):
                MODULE.estimate(archive, ["system.img"], 200, -1)


if __name__ == "__main__":
    unittest.main()
