import * as React from "react";

function SvgLogo(props: React.SVGProps<SVGSVGElement>) {
	return (
		<svg width={32} height={32} fill="none" xmlns="http://www.w3.org/2000/svg" {...props}>
			<path
				fillRule="evenodd"
				clipRule="evenodd"
				d="M30 8a4 4 0 01-4.807 3.918l-4.79 9.98a4 4 0 11-7.249.998L5.06 18.18a3 3 0 11.8-1.27l7.975 4.643A3.994 3.994 0 0117 20c.857 0 1.65.27 2.3.727l4.505-9.383A4 4 0 1130 8z"
				fill="url(#logo_svg__paint0_linear)"
			/>
			<path
				fillRule="evenodd"
				clipRule="evenodd"
				d="M17 6c0 .74-.2 1.433-.551 2.027l8.695 7.884a3.5 3.5 0 11-.873 1.234l-8.812-7.99a3.983 3.983 0 01-3.136.788L7.745 23.4a4 4 0 11-1.452-.39l4.623-13.594A4 4 0 1117 6z"
				fill="#096DD9"
			/>
			<defs>
				<linearGradient
					id="logo_svg__paint0_linear"
					x1={19}
					y1={9}
					x2={15}
					y2={28}
					gradientUnits="userSpaceOnUse">
					<stop stopColor="#C41D6D" />
					<stop offset={1} stopColor="#9C213E" />
				</linearGradient>
			</defs>
		</svg>
	);
}

export default SvgLogo;
